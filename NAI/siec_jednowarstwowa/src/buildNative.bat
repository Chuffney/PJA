gcc -O3 -c compatLayer.c
nasm -f win64 Vector.asm

ld -s --image-base 0x40000 --shared -o training.dll compatLayer.o Vector.obj msvcrt.dll
