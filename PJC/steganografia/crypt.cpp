#include <crypt.h>

#include <cstring>
#include <cstdio>
#include <filesystem>

#include <fileCheck.h>

namespace so
{
    static void encodePixel(sf::Image& image, unsigned x, unsigned y, uint32_t data)
    {
        sf::Color color = image.getPixel(x, y);
        uint32_t asInt = color.toInteger() & 0xFEFEFEFF;    //zero least significant bit of each colour (except alpha)
        asInt |= (data & 1) << 8;
        asInt |= (data & 2) << 15;
        asInt |= (data & 4) << 22;
        image.setPixel(x, y, sf::Color(asInt));
    }

    static void encodeByte(sf::Image& image, uint8_t data)
    {
        static unsigned x = 0;
        static unsigned y = 0;

        const unsigned width = image.getSize().x;

        for (unsigned i = 0; i < 3; i++)
        {
            encodePixel(image, x, y, data & 0b111);
            data >>= 3;
            if (++x == width)
            {
                x = 0;
                y++;
            }
        }
    }

    static inline std::string appendToPath(const char* filePath)
    {
        std::filesystem::path path(filePath);
        return path.parent_path().append(path.stem().concat("-encrypted").string() + path.extension().string()).string();   //insert "-encrypted" between stem and extension
    }

    void encrypt(const char* filePath, const char* message)
    {
        std::string path(filePath);
        const size_t messageLength = strlen(message);

        sf::Image image = {};
        image.loadFromFile(path);


        if (imageSizeCheck(image, messageLength))
        {
            fprintf(stderr, "the message is too long for this file\n");
            return;
        }

        encodeByte(image, messageLength);       //{
        encodeByte(image, messageLength >> 8);  //first encode length
        encodeByte(image, messageLength >> 16); //of the message...
        encodeByte(image, messageLength >> 24); //}

        for (uint32_t messageOffset = 0; messageOffset < messageLength; messageOffset++)
        {
            encodeByte(image, message[messageOffset]);  //...then the message itself - without string-terminator
        }

        image.saveToFile(appendToPath(filePath));
    }

    static uint8_t decodePixel(const sf::Image& image, unsigned x, unsigned y)
    {
        uint32_t pixel = image.getPixel(x, y).toInteger();
        return ((pixel & 0x100) >> 8) | ((pixel & 0x10000) >> 15) | ((pixel & 0x1000000) >> 22);
    }

    static uint8_t decodeByte(const sf::Image& image)
    {
        static unsigned x = 0;
        static unsigned y = 0;

        const unsigned width = image.getSize().x;

        uint8_t data = 0;
        for (unsigned i = 0; i < 3; i++)
        {
            uint8_t pixelVal = decodePixel(image, x, y);
            data |= pixelVal << (3 * i);
            if (++x == width)
            {
                x = 0;
                y++;
            }
        }
        return data;
    }

    void decrypt(const char* filePath)
    {
        sf::Image image = {};
        image.loadFromFile(filePath);

        uint32_t messageLength = decodeByte(image) | decodeByte(image) << 8 | decodeByte(image) << 16 | decodeByte(image) << 24;

        std::unique_ptr<uint8_t> message(new uint8_t[messageLength + 1]);

        for (uint32_t messageOffset = 0; messageOffset < messageLength; messageOffset++)
        {
            message.get()[messageOffset] = decodeByte(image);
        }
        message.get()[messageLength] = '\0'; //end of string
        std::printf("decrypted message: %s\n", message.get());
    }
}
