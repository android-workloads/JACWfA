# Define a variable for classpath
CLASS_PATH = ../../../build/classes/
ADD_CLASS_PATH = libs/*

LIBS_X86 = $(LIBS)/x86
LIBS_X86_64 = $(LIBS)/x86_64
OBJ_X86 = $(OBJ_LOCAL)/x86
OBJ_X86_64 = $(OBJ_LOCAL)/x86_64

CCFLAGS_X86 = -m32 -I"$(JAVA_HOME_X86_64)/include" -I"$(JAVA_HOME_X86_64)/include/linux" -Wall -c
CCFLAGS_X86_64 = -m64 -I"$(JAVA_HOME_X86_64)/include" -I"$(JAVA_HOME_X86_64)/include/linux" -fpic -Wall -c

LFLAGS_X86 = -m elf_i386
LFLAGS_X86_64 = -m elf_x86_64

OUT_LIB_NAMES = \
$(LIBS_X86)/libpasserJNIParam.so \
$(LIBS_X86_64)/libpasserJNIParam.so

# Define a virtual path for .class in the bin directory
vpath %.class $(CLASS_PATH)

all: create_dirs $(OUT_LIB_NAMES)

create_dirs:
	mkdir -p $(LIBS_X86)
	mkdir -p $(LIBS_X86_64)
	mkdir -p $(OBJ_X86)
	mkdir -p $(OBJ_X86_64)

$(LIBS_X86)/libpasserJNIParam.so: $(OBJ_X86)/passerJNIParam.o
	ld $(LFLAGS_X86) -shared -soname $@ -o $@  $^ -lc

$(LIBS_X86_64)/libpasserJNIParam.so: $(OBJ_X86_64)/passerJNIParam.o
	ld $(LFLAGS_X86_64) -shared -soname $@ -o $@  $^ -lc

#32 bit object files
$(OBJ_X86)/%.o: %.cpp
	g++ $(CCFLAGS_X86) -o $@ $<

#64 bit object files
$(OBJ_X86_64)/%.o: %.cpp
	g++ $(CCFLAGS_X86_64) -o $@ $<

clean:
	rm -rf $(LIBS_X86) $(LIBS_X86_64)
