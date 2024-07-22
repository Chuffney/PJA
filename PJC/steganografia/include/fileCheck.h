#ifndef STEGANOGRAFIAOBRAZOWA_FILECHECK_H
#define STEGANOGRAFIAOBRAZOWA_FILECHECK_H

#include <SFML/Graphics.hpp>

namespace so
{
    bool commonFileCheck(const char* filePath, bool checkWrite);
    bool imageSizeCheck(const char* filePath, const char* message);
    bool imageSizeCheck(const sf::Image& image, size_t messageLen);
    bool imageFormatCheck(const char* filePath);
}

#endif //STEGANOGRAFIAOBRAZOWA_FILECHECK_H
