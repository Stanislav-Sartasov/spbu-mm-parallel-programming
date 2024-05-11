#pragma once

#include <memory>
#include <string>

#include "PeerNode.h"
#include "TerminalRenderer.h"

class ChatApp {
public:
  ChatApp(const std::string &username, int port);
  void run();

private:
  std::string prompt(const std::string &message);
  void showInstructions() const;

  std::shared_ptr<PeerNode> peerNode_;
  std::unique_ptr<TerminalRenderer> renderer_;
};
