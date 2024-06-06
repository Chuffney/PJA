#include "ArrayList.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

static ArrayList fileRows;

static void initArrayList(ArrayList* list)
{
    list->array = malloc(128);
    list->size = 0;
    list->allocatedMemory = 128;
}

static char* readFile(const char* fileName, uint32_t* OutFileLength)
{
    FILE* file = fopen(fileName, "rb");
    fseek(file, 0, SEEK_END);
    uint32_t length = ftell(file);
    fseek(file, 0, SEEK_SET);
    char* buffer = malloc(length + 1);
    fread(buffer, 1, length, file);
    buffer[length] = '\0';
    fclose(file);
    *OutFileLength = length;
    return buffer;
}

static void writeFile(const char* fileName, char* buffer, uint32_t bufferLength)
{
    FILE* file = fopen(fileName, "w");
    fwrite(buffer, 1, bufferLength, file);
    fclose(file);
}

static inline bool isEOL(const char c)
{
    return c == '\n' || c == '\r';
}

static inline bool isDigit(const char c)
{
    return c >= '0' && c <= '9';
}

static void divideFileToRows(char* buffer, uint32_t bufferLength)
{
    uint32_t iterator = 0;
    while (true)
    {
        while (isEOL(buffer[iterator]))
        {
            buffer[iterator] = '\0';
            iterator++;
            if (iterator == bufferLength)
                return;
        }

        AL_add(&fileRows, (uint64_t) &buffer[iterator]);

        while (!isEOL(buffer[iterator]))
        {
            iterator++;
            if (iterator == bufferLength)
                return;
        }
    }
}

int compStrings(const char** arg1, const char** arg2)
{
    const char* s1 = *arg1;
    const char* s2 = *arg2;

    for (uint32_t i = 0;; i++)
    {
        if (isDigit(s1[i]) && isDigit(s2[i]))
        {
            int int1 = atoi(&s1[i]);
            int int2 = atoi(&s2[i]);

            if (int1 != int2)
                return int1 - int2;

            while (isDigit(s1[i + 1]))
                i++;
        } else if (s1[i] == s2[i])
        {
            if (s1[i] == '\0')
                return 0;

            continue;
        } else
        {
            if (s1[i] == '\0')
                return -1;
            if (s2[i] == '\0')
                return 1;
            return s1[i] - s2[i];
        }
    }
}

int main(void)
{
    initArrayList(&fileRows);

    uint32_t bufferLength;
    char* inputBuffer = readFile("in.txt", &bufferLength);
    divideFileToRows(inputBuffer, bufferLength);

    qsort(fileRows.array, fileRows.size, sizeof(char*), (int (*)(const void*, const void*)) compStrings);

    char* outputBuffer = malloc(bufferLength);
    char* offsetAddress = outputBuffer;
    for (uint32_t i = 0; i < fileRows.size; i++)
    {
        strcpy(offsetAddress, (char*) AL_get(&fileRows, i));
        offsetAddress += strlen(offsetAddress);
        *offsetAddress = '\n';
        offsetAddress++;
    }

    writeFile("out.txt", outputBuffer, offsetAddress - outputBuffer);

    free(outputBuffer);
    free(inputBuffer);
    free(fileRows.array);
    return 0;
}
