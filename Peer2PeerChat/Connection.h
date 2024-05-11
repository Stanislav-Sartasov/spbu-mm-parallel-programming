#pragma once

#include "Message.h"
#include <atomic>
#include <boost/asio.hpp>
#include <memory>
#include <thread>

class PeerNode;

class Connection : public std::enable_shared_from_this<Connection> {
public:
  Connection(boost::asio::ip::tcp::socket socket, PeerNode &peerNode);
  void start();
  void propagate(const Message &message);
  void close();

  boost::asio::ip::tcp::socket socket_;

private:
  void receiveMessages();
  void readMessage();

  PeerNode &peerNode_;
  std::atomic<bool> closed_;
  std::thread thread_;
};
