# Define a variable for classpath
CLASS_PATH = ../../../build/classes/
ADD_CLASS_PATH = libs/*

export JAVA_HOME_X86_64 = $(java_home)

export LIBS = $(MTTEST_NATIVE_BINARIES_DIR)/libs/
export OBJ_LOCAL = $(MTTEST_NATIVE_BINARIES_DIR)/obj/local/

DIRS = $(wildcard */)

# Define a virtual path for .class in the bin directory
vpath %.class $(CLASS_PATH)

all: create_dirs make_all_subdirs

create_dirs:
	mkdir -p $(LIBS)
	mkdir -p $(OBJ_LOCAL)

make_all_subdirs: $(DIRS)

$(DIRS):
	$(MAKE) -C $@ -f Makefile.mk

clean:
	rm -rf $(LIBS) $(OBJ_LOCAL)

.PHONY: all clean $(DIRS)