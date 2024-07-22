#include <fileCheck.h>

#include <cstring>
#include <filesystem>

namespace so
{
    //https://www.sfml-dev.org/documentation/2.5.1/classsf_1_1Image.php#a9e4f2aa8e36d0cabde5ed5a4ef80290b
    const char* supportedFormats[] = {".bmp", ".png", ".gif", ".tga"};

    bool commonFileCheck(const char* filePath, bool checkWrite)
    {
        namespace fs = std::filesystem;

        fs::path path = fs::path(filePath);
        if (!exists(path))
        {
            std::fprintf(stderr, "file %s does not exist\n", filePath);
            return true;
        }
        if (!is_regular_file(path))
        {
            std::fprintf(stderr, "%s is not a file", filePath);
            return true;
        }
        fs::perms permissions = fs::status(path).permissions();
        if ((permissions & fs::perms::others_read) == fs::perms::none)
        {
            std::fprintf(stderr, "cannot read from the file %s", filePath);
            return true;
        }
        if (checkWrite && (permissions & fs::perms::others_write) == fs::perms::none)
        {
            std::fprintf(stderr, "cannot write to file %s", filePath);
            return true;
        }

        return false;
    }

    bool imageSizeCheck(const char* filePath, const char* message)
    {
        if (commonFileCheck(filePath, true))
            return true;
        size_t messageLength = std::strlen(message);
        sf::Image image;
        image.loadFromFile(filePath);

        return imageSizeCheck(image, messageLength);
    }

    bool imageSizeCheck(const sf::Image& image, size_t messageLen)
    {
        if (messageLen > UINT32_MAX)
            return true;    //because encoding would break

        const uint8_t headerOverhead = 1 + (sizeof(uint32_t) * 8);

        sf::Vector2u dimensions = image.getSize();
        uint64_t totalBytes = ((uint64_t) dimensions.x * dimensions.y) * 3; //*3 because there are three channels (alpha is unused)

        uint64_t bytesNeeded = (messageLen * 8) + headerOverhead;

        return totalBytes < bytesNeeded;
    }

    bool imageFormatCheck(const char* filePath)
    {
        namespace fs = std::filesystem;

        fs::path path(filePath);
        fs::path extension = path.extension();

        for (const char* str : supportedFormats)
        {
            if (extension == str)
                return false;
        }
        return true;
    }
}
