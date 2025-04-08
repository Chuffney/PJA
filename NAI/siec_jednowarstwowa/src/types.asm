;NASM-style assembly header

struc Buffer
    .alignedBufPtr  resq 1
    .offsetListPtr  resq 1
    .size   resd 1
endstruc

struc Dataset
    .data   resd 32
    .nameId resd 1
endstruc

struc Neuron
    .weights:       resd 32
    .threshold:     resd 1
    .recognisedId:  resd 1
endstruc
