#include <stdlib.h>
#include <stdint.h>
#include <string.h>

#pragma pack(push, 4)
typedef struct
{
    float data[32];
    uint32_t nameId;
} Dataset;

typedef struct
{
    union
    {
        float* alignedFloatPtr;
        uint8_t* alignedBytePtr;
    };
    float** offsetListPtr;
    uint32_t size;
} ABuffer;

typedef struct
{
    float weights[32];
    float threshold;
    uint32_t recognisedId;
} Neuron;
#pragma pack(pop)

extern void NE_train(Neuron*, ABuffer*, ABuffer*);

void Java_Neuron_train(void* a, void* b, Neuron* this, ABuffer* trainingData, ABuffer* testData)
{
    NE_train(this, trainingData, testData);
}

void Java_Neuron_getWeights(void* a, void* b, Neuron* this, float** weights)
{
    memcpy(&weights[0][4], this->weights, 26 * 4);
}

float Java_Neuron_getThreshold(void* a, void* b, Neuron* this)
{
    return this->threshold;
}

ABuffer* initBuffer(int32_t elementCount)
{
    uint32_t alignedSize;
    for (alignedSize = 1; alignedSize < sizeof(Dataset); alignedSize <<= 1);

    ABuffer* result = malloc(sizeof(ABuffer));
    result->size = 0;
    result->offsetListPtr = malloc(elementCount * sizeof(Dataset*));
    result->alignedFloatPtr = calloc(elementCount * alignedSize, 1);

    return result;
}

ABuffer* Java_Neuron_initBuffer(void* a, void* b, int elementCount)
{
    return initBuffer(elementCount);
}

void addDataset(ABuffer* this, float* data, uint32_t nameId)
{
    uint32_t alignedSize;
    for (alignedSize = 1; alignedSize < sizeof(Dataset); alignedSize <<= 1);

    uint32_t newOffset = this->size++;
    void* newAddress = &this->alignedBytePtr[newOffset * alignedSize];
    this->offsetListPtr[newOffset] = newAddress;
    memcpy(newAddress, data, 26 * 4);
    ((uint32_t*) newAddress)[32] = nameId;
}

void Java_Neuron_addDataset(void* a, void* b, ABuffer* this, float** data, uint32_t nameId)
{
    addDataset(this, &data[0][4], nameId);
}

Neuron* initNeuron(uint32_t recognisedId)
{
    Neuron* result = calloc(sizeof(Neuron), 1);
    result->recognisedId = recognisedId;
    return result;
}

Neuron* Java_Neuron_initNeuron(void* a, void* b, int recognisedId) {
    return initNeuron(recognisedId);
}
