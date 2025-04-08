;x86_64, NASM-style assembly
;MS calling convention

%include "types.asm"

global NE_train
global __main

YMMWORD equ 32
;typedef float[32] Vector

defAlpha equ __?float32?__(0.01)


section .data
align YMMWORD
alpha:  times 8 dd defAlpha

section .text

NE_train:   ;void train(Neuron* this, Buffer* data, Buffer* tests)
    push rdi
    mov r9, rbx
    mov r10, rbp
    mov r11, rsi

    mov rsi, rdx
    mov rdi, r8

    vmovups ymm0, [rcx + Neuron.weights]
    vmovups ymm1, [rcx + Neuron.weights + YMMWORD]
    vmovups ymm2, [rcx + Neuron.weights + 2 * YMMWORD]
    vmovups ymm3, [rcx + Neuron.weights + 3 * YMMWORD]

TR_accLoop:    ;while(correctAnswers < 0.99f)
        xor ebx, ebx    ;correct
        xor ebp, ebp    ;iterator

    TR_datasetLoop: ;for dataset
            mov rdx, [rsi + Buffer.offsetListPtr]
            mov rdx, [rdx + rbp * 8]

            mov r8d, [rdx + Dataset.nameId]

            call learn

            add ebx, eax
            inc ebp

            cmp ebp, [rsi + Buffer.size]
            jb TR_datasetLoop

        xor ebp, ebp
    TR_testLoop:    ;for testset
            mov rdx, [rdi + Buffer.offsetListPtr]
            mov rdx, [rdx + rbp * 8]

            vmovups ymm4, [rdx]
            vmovups ymm5, [rdx + YMMWORD]
            vmovups ymm6, [rdx + 2 * YMMWORD]
            vmovups ymm7, [rdx + 3 * YMMWORD]

            call computeNet

            comiss xmm8, [rcx + Neuron.threshold]
            setae r8b    ;decision

            mov eax, [rdx + Dataset.nameId]
            cmp eax, [rcx + Neuron.recognisedId]
            setne al ;inverted correct answer

            xor al, r8b ;decision XNOR correct
            movzx eax, al

            add ebx, eax    ;correctCount += ~(decision ^ correct)
            inc ebp

            cmp ebp, [rdi + Buffer.size]
            jb TR_testLoop

        add ebp, [rsi + Buffer.size]    ;count of all conducted tests in ebp
        sub ebx, ebp    ;negative incorrectly classified cases
        neg ebx
        imul ebx, ebx, 50
        cmp ebx, ebp
        ja TR_accLoop

    vmovups [rcx], ymm0
    vmovups [rcx + YMMWORD], ymm1
    vmovups [rcx + 2 * YMMWORD], ymm2
    vmovups [rcx + 3 * YMMWORD], ymm3

    mov rbx, r9
    mov rbp, r10
    mov rsi, r11
    pop rdi
    ret

learn:
NE_learn:   ;bool learn(Neuron* this, Vector* input, unsigned correctId)
    mov eax, [rcx + Neuron.recognisedId]
    cmp eax, r8d
    sete al    ;correct answer


    vmovups ymm4, [rdx]
    vmovups ymm5, [rdx + YMMWORD]
    vmovups ymm6, [rdx + 2 * YMMWORD]
    vmovups ymm7, [rdx + 3 * YMMWORD]

    call computeNet

    comiss xmm8, [rcx + Neuron.threshold]   ;net
    setae r8b   ;decision
    sub al, r8b ;delta
    je learn_ret    ;if (decision == correct) return true;

    vmovaps ymm9, [alpha]   ;prefetch alpha and hope for the CPU to carry on executing in the meantime

    movsx eax, al
    and eax, 0x80000000
    or eax, __?float32?__(1.0)
    movd xmm8, eax

    movsldup xmm8, xmm8
    punpckldq xmm8, xmm8
    vperm2f128 ymm8, ymm8, ymm8, 0  ;delta in ymm8

    vmulps ymm8, ymm9   ;delta * alpha

    vmulps ymm4, ymm8
    vmulps ymm5, ymm8
    vmulps ymm6, ymm8
    vmulps ymm7, ymm8   ;all values multiplied by delta * alpha

    vaddps ymm0, ymm4
    vaddps ymm1, ymm5
    vaddps ymm2, ymm6
    vaddps ymm3, ymm7

    movss xmm9, [rcx + Neuron.threshold]
    subss xmm9, xmm8
    movss [rcx + Neuron.threshold], xmm9
    ;call normalise
    xor eax, eax
    ret
learn_ret:
    sete al
    movzx eax, al
    ret

computeNet:
NE_computeNet:  ;float computeNet(Neuron* this, Vector* input)
    ;ymm0 - ymm3    weights
    ;ymm4 - ymm7    input
    ;xmm8 return    net

    vmulps ymm8, ymm0, ymm4
    vmulps ymm9, ymm1, ymm5
    vmulps ymm10, ymm2, ymm6
    vmulps ymm11, ymm3, ymm7

    vaddps ymm8, ymm9
    vaddps ymm10, ymm11
    vaddps ymm8, ymm10

    vhaddps ymm8, ymm8
    vextractf128 xmm9, ymm8, 1
    addps xmm8, xmm9
    haddps xmm8, xmm8
    ret

normalise:
VC_normalise:   ;void normalise(aligned Vector*)
    vmulps ymm4, ymm0, ymm0   ;square all values
    vmulps ymm5, ymm1, ymm1
    vmulps ymm6, ymm2, ymm2
    vmulps ymm7, ymm3, ymm3

    vaddps ymm4, ymm5   ;sum all values
    vaddps ymm6, ymm7
    vaddps ymm4, ymm6

    vhaddps ymm4, ymm4
    vextractf128 xmm5, ymm4, 1
    addps xmm4, xmm5
    haddps xmm4, xmm4   ;four sums of squares in xmm4

    rsqrtps xmm4, xmm4
    vperm2f128 ymm4, ymm4, ymm4, 0

    vmulps ymm0, ymm4
    vmulps ymm1, ymm4
    vmulps ymm2, ymm4
    vmulps ymm3, ymm4

__main:
    ret
