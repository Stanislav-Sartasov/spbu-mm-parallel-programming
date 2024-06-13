#include "ChatApp.h"
#include <iostream>
#include <string>

int main() {
  std::cout << "Welcome to the P2P Chat Application" << std::endl;

  std::string username;
  std::cout << "Enter your name: ";
  std::getline(std::cin, username);

  std::string portInput;
  std::cout << "Enter your port: ";
  std::getline(std::cin, portInput);
  int port = std::stoi(portInput);

  ChatApp chatApp(username, port);
  chatApp.run();

  return 0;
}
