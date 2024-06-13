#pragma once

#include "Connection.h"
#include "MessageLog.h"
#include "TerminalRenderer.h"
#include <boost/asio.hpp>
#include <memory>
#include <mutex>
#include <set>
#include <string>
#include <vector>

class PeerNode {
public:
  PeerNode(const std::string &username, int port);
  void setRenderer(TerminalRenderer *renderer);
  void start();
  void connect(const std::string &host, int port);
  void shutdown();
  void sendMessage(const std::string &message);
  void processIncomingMessage(const Message &message);

private:
  void acceptConnections();
  void handleAccept(std::shared_ptr<Connection> connection,
                    const boost::system::error_code &error);
  void propagateMessage(const Message &message);
  void renderChatLog();

  std::string username_;
  int port_;
  boost::asio::io_context ioContext_;
  boost::asio::ip::tcp::acceptor acceptor_;
  std::vector<std::shared_ptr<Connection>> connections_;
  MessageLog messageLog_;
  TerminalRenderer *renderer_;
  std::mutex mutex_;
  std::set<std::string> sentMessageContents_; // Track sent message contents
};
