;x86_64, NASM-style assembly
;uses the C Standard Library
;MS calling convention

extern fopen
extern fread
extern fseek
extern ftell
extern fclose
extern malloc
extern free
extern printf
extern scanf
extern gets
extern getchar
extern strtok
extern strcmp
extern atof
extern memset
extern qsort

extern initEnumerator
extern enumerate
extern getName

global main

section .rdata
uintFormat: db '%u', 0
fileMode:   db 'r', 0
delimiters: db ' ', 0x9, 0xA, 0
trainingFile:   db 'iris_training.txt', 0
testFile:       db 'iris_test.txt', 0
fileErrorMsg:   db 'failed to load a file', 0
kPrompt:        db 'input k: ', 0
errorRateMsg:   db 'test error rate: %u/%u (%.2f%%)', 0xA, 0
userInputMsg:   db 'input a %u-dimensional vector: ', 0
stopStr:        db 'stop', 0
resultStr:      db 'classified as: %s', 0xA, 0xA, 0

section .bss
trainingDataPtr:    resq 1
enumerator: resq 4
paramCount: resd 1
lineCount:  resd 1
kNumber:    resd 1

section .text

main:
    push rbx
    push rbp
    push rdi
    push rsi
    push r15
    sub rsp, 0x30

    mov rcx, enumerator
    call initEnumerator

    mov rcx, trainingFile
    call readFile   ;readFile("iris_training.txt");

    test rax, rax
    jz MN_file_fail ;if (fileBuffer == NULL) fail();

    mov rbx, rax    ;preserve file buffer
    mov rcx, rax
    call countColumns
    mov [paramCount], eax ;paramCount = countColumns(fileBuffer);

    mov rcx, rbx
    call countLines ;lines = countLines(fileBuffer);
    mov [lineCount], eax
    mul dword [paramCount]  ;(lines * paramCount)
    shl eax, 2  ;(lines * paramCount * 4)

    mov ecx, eax
    call malloc
    mov [trainingDataPtr], rax  ;trainingDataPtr = malloc(lines * paramCount * sizeof(float));

    mov rcx, rbx
    mov rdx, [trainingDataPtr]
    mov r8d, [lineCount]
    call parseDataFile

    mov rcx, rbx
    call free

    mov rcx, kPrompt
    call printf     ;printf("input k: ");

    mov rcx, uintFormat
    mov rdx, kNumber
    call scanf      ;prompt user for k-number

    call discardExtraInput

    mov rcx, testFile
    call readFile   ;testBuffer = readFile("iris_test.txt");

    mov rbx, rax    ;preserve test file buffer
    mov rcx, rax
    call countLines
    mov ebp, eax    ;preserve test count

    mul dword [paramCount]
    lea rcx, [rax * 4]  ;(test line count * param count * sizeof(float))
    call malloc
    mov rdi, rax    ;preserve test data buffer

    mov rcx, rbx
    mov rdx, rax
    mov r8d, ebp
    call parseDataFile

    mov rcx, rbx
    call free

    mov ecx, [kNumber]
    shl ecx, 3
    call malloc ;buffer for k nearest pairs (distance, id)
    mov rbx, rax

    mov esi, ebp
    xor r15d, r15d
MN_testLoop:
        dec esi

        mov eax, [paramCount]
        mul esi
        lea rcx, [rdi + rax * 4]
        mov rdx, rbx
        call findNearest

        mov ecx, eax
        mov eax, [paramCount]
        lea edx, [esi + 1]
        mul edx
        cmp ecx, [rdi + rax * 4 - 4]

        setne dl    ;equal if classified correctly
        movzx edx, dl
        add r15d, edx    ;errors += correct ? 0 : 1;

        test esi, esi
        jnz MN_testLoop

    mov ecx, __?float32?__(100.0)
    movd xmm2, ecx
    cvtsi2ss xmm0, r15d ;errors
    cvtsi2ss xmm1, ebp  ;number of test conducted
    divss xmm0, xmm1    ;error rate
    mulss xmm0, xmm2    ;100 for displaying as percentage
    cvtss2sd xmm0, xmm0
    movq r9, xmm0

    mov rcx, errorRateMsg
    mov edx, r15d
    mov r8d, ebp
    call printf

    mov ecx, [paramCount]
    lea ebp, [ecx - 1]
    shl ecx, 2
    call malloc     ;malloc((paramCount - 1) * sizeof(float));
    mov r15, rax    ;preserve user input data buffer

MN_userInputLoop:
        mov rcx, userInputMsg
        mov edx, ebp
        call printf     ;printf("input a %u-dimensional vector: ", paramCount);

        mov rcx, rdi    ;reuse test data buffer
        call gets

        mov rcx, rdi
        mov rdx, stopStr
        call strcmp
        test eax, eax   ;if input = "stop" return;
        jz MN_ret

        xor esi, esi    ;scanLoop iterator
    MN_UIL_scanLoop:
            xor ecx, ecx
            test esi, esi
            cmovz rcx, rdi
            mov rdx, delimiters
            call strtok

            mov rcx, rax
            call atof
            cvtsd2ss xmm0, xmm0
            movd [r15 + rsi * 4], xmm0

            inc esi
            cmp esi, ebp
            jb MN_UIL_scanLoop
        mov rcx, r15
        mov rdx, rbx
        call findNearest

        mov rcx, enumerator
        mov edx, eax
        call getName

        mov rcx, resultStr
        mov rdx, rax
        call printf

        jmp MN_userInputLoop
MN_ret:
    add rsp, 0x30
    pop r15
    pop rsi
    pop rdi
    pop rbp
    pop rbx
    ret

MN_file_fail:
    mov rcx, fileErrorMsg
    call printf
    jmp MN_ret


readFile:   ;char* readFile(const char* filePath)
    push rbx
    push rbp
    sub rsp, 0x20

    mov rdx, fileMode   ;"r"
    call fopen          ;fopen(filePath, "r");

    test rax, rax
    jz RF_ret      ;if (!file) return 0;

    mov rbx, rax    ;preserve FILE*

    mov rcx, rax
    xor eax, eax
    mov r8d, 2
    call fseek  ;fseek(file, 0, SEEK_END);

    mov rcx, rbx
    call ftell  ;ftell(file);
    mov ebp, eax    ;preserve file length

    mov rcx, rbx
    xor edx, edx
    xor r8d, r8d
    call fseek  ;fseek(file, 0, SEEK_SET);

    lea rcx, [rbp + 1]
    call malloc ;malloc(fileLength + 1);
    mov byte [rax + rbp], 0 ;buffer[length] = '\0';

    mov rcx, rax
    mov edx, 1
    mov r8d, ebp
    mov r9, rbx
    mov rbp, rax
    call fread  ;fread(buffer, 1, fileLength, file);

    mov rcx, rbx
    call fclose ;fclose(file);

    mov rax, rbp    ;return buffer;

RF_ret:
    add rsp, 0x20
    pop rbp
    pop rbx
    ret


countColumns: ;unsigned int countColumns(const char* buffer)
    xor eax, eax    ;column count = 0

    CC_skipWhitespace:  ;for(int i = 0; buffer[i] != ' ' && buffer[i] != '\t'; i++);
        mov dl, [rcx]
        call isWhitespace
        jne CC_SW_break
    CC_SW_iterate:
        inc rcx
        jmp CC_skipWhitespace
CC_SW_break:

    cmp dl, 0xA
    je CC_ret   ;if (buffer[i] == '\n') return columnCount;

    inc eax ;columnCount++;
    CC_skipChars:   ;for(int i = ... ; isChar(buffer[i]); i++);
        inc rcx
        mov dl, [rcx]
        call isWhitespace
        je CC_SW_iterate
        cmp dl, 0xA ;if (buffer[i] == '\n') return columnCount;
        jne CC_skipChars
CC_ret:
    ret

isWhitespace:   ;ZFlag isWhitespace(const char c); custom calling
    cmp dl, ' '
    je IW_ret
    cmp dl, 0x9 ;'\t'
IW_ret:
    ret ;return (c == ' ' || c == '\t') ? 1 : 0;

countLines: ;unsigned countLines(const char* buffer)
    xor eax, eax
    xor r8d, r8d

    CL_loop: ;while (buffer[i] != '\0')
        mov dl, [rcx]
        cmp dl, 0xA
        sete r8b
        add eax, r8d    ;if (buffer[i] == '\n') lines++;
        inc rcx
        test dl, dl
        jnz CL_loop
    ret

convertCommas:  ;void convertCommas(char* textBuffer)
    mov dl, [rcx]
    cmp dl, ','
    jne CVT_skip
    mov byte [rcx], '.'
CVT_skip:
    inc rcx
    test dl, dl
    jnz convertCommas
    ret

parseDataFile:  ;void parseDataFile(char* fileBuffer, vector* dataBuffer, int lines)
    push rbx
    push rbp
    push rsi
    push rdi
    push r15
    sub rsp, 0x20

    mov rsi, rcx    ;fileBuffer
    mov r15, rdx    ;dataBuffer
    mov ebx, r8d    ;lines
    mov ebp, [paramCount]   ;columns
    dec ebp

    call convertCommas

PDF_lineLoop:
        dec ebx
        xor edi, edi    ;column iterator
    PDF_columnLoop:
            mov rcx, rsi
            mov rdx, delimiters
            call strtok

            xor esi, esi    ;so that strtok is called with NULL as 1st arg
            mov rcx, rax
            call atof   ;parse str to double
            cvtsd2ss xmm0, xmm0 ;double -> float

            lea rax, [rbp + 1]
            mul ebx     ;(current line * columnCount)
            mov rcx, r15
            lea rcx, [rcx + rdi * 4]
            movd [rcx + rax * 4], xmm0

            inc edi
            cmp edi, ebp
            jb PDF_columnLoop
        xor ecx, ecx
        mov rdx, delimiters
        call strtok     ;strtok(NULL, " \t\n");

        mov rcx, enumerator
        mov rdx, rax
        call enumerate
        mov ecx, eax

        lea rax, [rbp + 1]
        mul ebx
        mov rdx, r15
        lea rdx, [rdx + rdi * 4]
        mov [rdx + rax * 4], ecx

        test ebx, ebx
        jnz PDF_lineLoop

    add rsp, 0x20
    pop r15
    pop rdi
    pop rsi
    pop rbp
    pop rbx
    ret

calcDistance:   ;float calcDistance(vector* a, vector* b, int paramCount)
    xorps xmm0, xmm0
    dec r8d

CD_loop:
        dec r8d
        movd xmm1, [rcx + r8 * 4]
        subss xmm1, [rdx + r8 * 4] ;a[i] - b[i]
        mulss xmm1, xmm1    ;(a[i] - b[i])^2
        addss xmm0, xmm1    ;sum += (a[i] - b[i])^2
        test r8d, r8d
        jnz CD_loop
    ret

findNearest:    ;int findNearest(vector* v, vector[] distanceBuffer)
    push rbx
    push rbp
    sub rsp, 0x20
    mov rbx, rdx
    mov rbp, rcx

    mov rcx, rdx
    mov edx, 0x7f
    mov r8d, [kNumber]
    shl r8d, 3
    call memset

    mov rcx, rbp
    mov eax, [lineCount]
    mov r8d, [paramCount]
    mov r9, [trainingDataPtr]
    mov r10, rbx
    mul r8d

FN_lineLoop:
        sub eax, r8d

        lea rdx, [r9 + rax * 4]
        call calcDistance
        mov r8d, [paramCount]

        xorps xmm2, xmm2
        xor edx, edx
    FN_distBufferLoop:  ;finds the greatest distance in distanceBuffer
            movd xmm1, [r10 + rdx * 8]
            comiss xmm1, xmm2   ;if (distBuf[i] > max)
            jb FN_distBufferLoopControl

            movss xmm2, xmm1
            mov r11d, edx

    FN_distBufferLoopControl:
            inc edx
            cmp edx, [kNumber]
            jb FN_distBufferLoop

        comiss xmm0, xmm2
        ja FN_lineLoopControl   ;if (distance > max of distBuf)

        movd [r10 + r11 * 8], xmm0  ;distBuf[max].dist = distance
        lea rdx, [r9 + rax * 4]
        mov edx, [rdx + r8 * 4 - 4]
        mov [r10 + r11 * 8 + 4], edx    ;distBuf[max].id = id

FN_lineLoopControl:
        test eax, eax
        jnz FN_lineLoop
    mov rcx, r10
    mov edx, [kNumber]
    mov r8d, 8
    mov r9, compareDistanceEntries
    call qsort

    mov eax, [kNumber]
    dec eax
    mov ecx, eax    ;max index
    mov edx, 1    ;current count
    mov r8d, 1    ;max count
    mov r9d, [rbx + rax * 8 + 4] ;previous id

FN_classificationLoop:
        dec eax
        mov r10d, [rbx + rax * 8 + 4]   ;current id
        cmp r10d, r9d
        je FN_CL_nextEqual

        cmp edx, r8d
        cmova r8d, edx
        cmova ecx, eax
        inc ecx
        xor edx, edx
    FN_CL_nextEqual:
        inc edx

    FN_classificationLoopControl:
        mov r9d, r10d
        test eax, eax
        jnz FN_classificationLoop

    cmp edx, r8d
    cmova ecx, eax

    mov eax, [rbx + rcx * 8 + 4]
    add rsp, 0x20
    pop rbp
    pop rbx
    ret

compareDistanceEntries: ;int compareDistanceEntries(void* a, void* b)
    mov rcx, [rcx]
    ror rcx, 32
    mov rdx, [rdx]
    ror rdx, 32

    cmp ecx, edx
    jne CDE_ret

    shr rcx, 32
    shr rdx, 32
CDE_ret:
    sub ecx, edx
    mov eax, ecx
    ret

discardExtraInput:
    sub rsp, 0x20
DEI_loop:
    call getchar
    cmp al, 0xA ;'\n'
    je DEI_ret
    inc eax
    jnz DEI_loop   ;zero if eax was 0xFFFFFFFF before inc
DEI_ret:
    add rsp, 0x20
    ret
