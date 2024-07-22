#include <cstdio>
#include <cstring>

#include <filesystem>
#include <format>
#include <chrono>

#include <SFML/Graphics.hpp>

#include <help.h>
#include <fileCheck.h>
#include <crypt.h>

namespace so
{
    enum operation
    {
        NONE,
        UNKNOWN,
        HELP,
        INFO,
        ENCRYPT,
        DECRYPT,
        CHECK
    };

    static inline so::operation parseFlags(int argc, const char** argv)
    {
        if (argc == 1)  //no arguments provided - defaults to help
            return HELP;

        std::string flagStr(argv[1]);
        unsigned int expectedArgs = -1;
        so::operation returnedOp;

        if (flagStr == "-i" || flagStr == "--info")
        {
            expectedArgs = 3;
            returnedOp = INFO;
        } else if (flagStr == "-e" || flagStr == "--encrypt")
        {
            expectedArgs = 4;
            returnedOp = ENCRYPT;
        } else if (flagStr == "-d" || flagStr == "--decrypt")
        {
            expectedArgs = 3;
            returnedOp = DECRYPT;
        } else if (flagStr == "-c" || flagStr == "--check")
        {
            expectedArgs = 4;
            returnedOp = CHECK;
        } else if (flagStr == "-h" || flagStr == "--help")
        {
            expectedArgs = 2;
            returnedOp = HELP;
        }
        else
        {
            return UNKNOWN;
        }


        if (argc != expectedArgs)
        {
            std::fprintf(stderr, "Invalid number of arguments (expected %d, received %d)\n", expectedArgs - 1, argc - 1);
            return NONE;
        } else
            return returnedOp;
    }

    static void info(const char* filePath)
    {
        if (commonFileCheck(filePath, false))
            return;

        namespace fs = std::filesystem;

        fs::path path = fs::path(filePath);
        fs::path extension = path.extension();
        size_t size = file_size(path);
        std::string lastModified = std::format("{}", last_write_time(path));

        sf::Image sfImage = {};
        sfImage.loadFromFile(filePath);
        sf::Vector2u imageDimensions = sfImage.getSize();

        std::printf("format: %ls\nsize: %llu bytes\ndimensions: %ux%u\nlast modified: %s\n",
                    extension.c_str(), size, imageDimensions.x, imageDimensions.y, lastModified.c_str());
    }

    int main(int argc, const char** argv)
    {
        so::operation op = so::parseFlags(argc, argv);

        const char* fileArg;
        if (op >= INFO) //file path expected in the second argument
            fileArg = argv[2];

        switch (op)
        {
            case NONE:
                break;
            case INFO:
                so::info(fileArg);
                break;
            case ENCRYPT:
                so::encrypt(fileArg, argv[3]);
                break;
            case DECRYPT:
                so::decrypt(fileArg);
                break;
            case CHECK:
                if (so::imageFormatCheck(fileArg))
                    std::printf("unsupported image format\n");
                else if (so::imageSizeCheck(fileArg, argv[3]))
                    std::printf("the message is too long for this file\n");
                else
                    std::printf("the message can be encoded in this file\n");
                break;
            case HELP:
                std::printf(so::helpMessage);
                break;
            case UNKNOWN:
                std::fprintf(stderr, "unrecognized flag\n");
                break;
        }

        return 0;
    }
}

int main(int argc, const char** argv)
{
    return so::main(argc, argv);
}
