#ifndef STEGANOGRAFIAOBRAZOWA_CRYPT_H
#define STEGANOGRAFIAOBRAZOWA_CRYPT_H

namespace so
{
    void encrypt(const char* filePath, const char* message);
    void decrypt(const char* filePath);
}

#endif //STEGANOGRAFIAOBRAZOWA_CRYPT_H
