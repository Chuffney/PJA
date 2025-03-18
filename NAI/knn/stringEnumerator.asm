;x86_64, NASM-style assembly
;uses the C Standard Library
;MS calling convention

extern malloc
extern realloc
extern strcmp
extern strcpy
extern strlen

global initEnumerator   ;void initEnumerator(Enumerator*)
global enumerate        ;int enumerate(Enumerator*, const char* str)
global getName          ;char* getName(Enumerator*, unsigned int id)

struc Enumerator
    .buffer: resq 1
    .offsetTable: resq 1
    .size:  resd 1
    .allocatedBuffer: resd 1
    .allocatedOffset: resd 1
endstruc

defaultOffsetSize equ 8
defaultBufferSize equ 128

section .text

initEnumerator: ;void initEnumerator(Enumerator*)
    push rbx
    sub rsp, 0x20

    mov rbx, rcx
    mov ecx, defaultBufferSize
    mov [rbx + Enumerator.allocatedBuffer], ecx
    call malloc
    mov [rbx + Enumerator.buffer], rax

    mov ecx, defaultOffsetSize
    mov [rbx + Enumerator.allocatedOffset], ecx
    call malloc
    mov [rbx + Enumerator.offsetTable], rax

    mov dword [rbx + Enumerator.size], 0

    add rsp, 0x20
    pop rbx
    ret


enumerate:  ;int enumerate(Enumerator* this, const char* str)
    push rbx
    push rbp
    push rdi
    push rsi
    sub rsp, 0x20

    mov rdi, rcx    ;{preserve args
    mov rsi, rdx    ;}

    xor ebx, ebx    ;iterator
    mov ebp, [rcx + Enumerator.size]    ;size

    test ebp, ebp
    jnz EN_loopControl  ;special case if size == 0

    mov rcx, rsi
    call strlen
    jmp EN_writeBufferSkip
EN_loop:
        mov rax, [rdi + Enumerator.offsetTable]
        mov eax, [rax + rbx * 4]
        mov rcx, [rdi + Enumerator.buffer]
        add rcx, rax
        mov rdx, rsi
        call strcmp ;strcmp(buffer[offset[i]], str)
        test al, al
        mov eax, ebx
        jz EN_ret

        inc ebx
EN_loopControl:
        cmp ebx, ebp
        jb EN_loop

EN_checkBuffer:
    mov rax, [rdi + Enumerator.offsetTable]
    mov ebx, [rax + rbp * 4 - 4]    ;offset of last string
    mov rcx, [rdi + Enumerator.buffer]
    add rcx, rbx
    call strlen

    add ebx, eax    ;used memory now

    mov rcx, rsi
    call strlen
    lea rbx, [rbx + rax + 2]    ;memory needed to fit str

    mov edx, [rdi + Enumerator.allocatedBuffer]
    cmp ebx, edx
    jbe EN_writeBuffer

    mov rcx, [rdi + Enumerator.buffer]
    shl edx, 1  ;allocated *= 2;
    mov [rdi + Enumerator.allocatedBuffer], edx
    call realloc

    mov [rdi + Enumerator.buffer], rax
    jmp EN_checkBuffer
EN_writeBuffer:
    sub rbx, rax
    dec rbx
EN_writeBufferSkip:
    mov rcx, [rdi + Enumerator.buffer]
    add rcx, rbx
    mov rdx, rsi
    call strcpy

    mov edx, [rdi + Enumerator.allocatedOffset]
    mov rcx, [rdi + Enumerator.offsetTable]
    cmp ebp, edx    ;if (size < allocatedOffset)
    jb EN_writeOffset

    shl edx, 1  ;allocated *= 2;
    call realloc

    mov [rdi + Enumerator.offsetTable], rax
EN_writeOffset:
    mov eax, ebp    ;return new id
    mov [rcx + rax * 4], ebx
    inc ebp
    mov [rdi + Enumerator.size], ebp

EN_ret:
    add rsp, 0x20
    pop rsi
    pop rdi
    pop rbp
    pop rbx
    ret

getName:    ;char* getName(Enumerator* this, unsigned int id)
    mov rax, [rcx + Enumerator.offsetTable]
    mov eax, [rax + rdx * 4]
    mov rdx, [rcx + Enumerator.buffer]
    add rax, rdx
    ret
