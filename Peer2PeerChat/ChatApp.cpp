#include "ChatApp.h"
#include "PeerNode.h"
#include "TerminalRenderer.h"
#include <iostream>
#include <stdexcept>
#include <thread>

ChatApp::ChatApp(const std::string &username, int port)
    : renderer_(std::make_unique<TerminalRenderer>()) {
  peerNode_ = std::make_shared<PeerNode>(username, port);
  peerNode_->setRenderer(renderer_.get());
}

void ChatApp::run() {
  showInstructions();

  std::thread peerThread(&PeerNode::start, peerNode_);

  std::string line;
  while (std::getline(std::cin, line)) {
    if (line.starts_with("/")) {
      std::string command = line.substr(1);
      if (command == "instructions") {
        showInstructions();
      } else if (command == "quit") {
        peerNode_->shutdown();
        break;
      } else if (command.starts_with("link ")) {
        try {
          int port = std::stoi(command.substr(5));
          peerNode_->connect("127.0.0.1", port);
          std::cout << "Linked to peer at 127.0.0.1:" << port << std::endl;
        } catch (const std::invalid_argument &e) {
          std::cerr << "Invalid port number." << std::endl;
        }
      } else {
        std::cout << "Unknown command: " << command << std::endl;
      }
    } else {
      peerNode_->sendMessage(line);
    }
  }

  peerThread.join();
  std::cout << "Thanks for using the chat. Goodbye!" << std::endl;
}

std::string ChatApp::prompt(const std::string &message) {
  std::string input;
  std::cout << message;
  std::getline(std::cin, input);
  return input;
}

void ChatApp::showInstructions() const {
  std::cout
      << "========================== Instructions =========================="
      << std::endl;
  std::cout << "Commands:" << std::endl;
  std::cout << "/link <port> - connect to another peer using their port"
            << std::endl;
  std::cout << "/quit - leave the chat" << std::endl;
  std::cout << "/instructions - show this message again" << std::endl;
  std::cout << "Any other input is considered a message for other peers."
            << std::endl;
  std::cout
      << "==================================================================="
      << std::endl;
}
