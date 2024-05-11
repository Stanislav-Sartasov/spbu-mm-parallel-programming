CXX = g++
CXXFLAGS = -std=c++20 -Wall -Wextra

SRCS = main.cpp \
       ChatApp.cpp \
       PeerNode.cpp \
       Connection.cpp \
       TerminalRenderer.cpp \
       Message.cpp \
       MessageLog.cpp

OBJS = $(SRCS:.cpp=.o)

TARGET = chat

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(OBJS)

%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

clean:
	rm -f $(OBJS) $(TARGET)

.PHONY: all clean

