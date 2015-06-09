################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
O_SRCS += \
../execute.o \
../mish.o \
../parser.o \
../sighant.o 

C_SRCS += \
../execute.c \
../mish.c \
../parser.c \
../sighant.c 

OBJS += \
./execute.o \
./mish.o \
./parser.o \
./sighant.o 

C_DEPS += \
./execute.d \
./mish.d \
./parser.d \
./sighant.d 


# Each subdirectory must supply rules for building sources it contributes
%.o: ../%.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross GCC Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


