#ifndef STEGANOGRAFIAOBRAZOWA_HELP_H
#define STEGANOGRAFIAOBRAZOWA_HELP_H

namespace so
{
    const char helpMessage[] = "usage: steg flag [file] [message]\n"\
    "flags:\n"\
    "-i --info\tprint file info\n"\
    "-e --encrypt\tencrypt a message in a file\n"\
    "-d --decrypt\tdecrypt a message from a file\n"\
    "-c --check\tcheck if a given message can be encrypted in a file\n"\
    "-h --help\tprint this message\n";
}

#endif //STEGANOGRAFIAOBRAZOWA_HELP_H
