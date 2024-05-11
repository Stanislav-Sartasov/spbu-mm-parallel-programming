#include "Connection.h"
#include "PeerNode.h"
#include <boost/asio.hpp>
#include <boost/asio/streambuf.hpp>
#include <iostream>
#include <nlohmann/json.hpp>

using json = nlohmann::json;

Connection::Connection(boost::asio::ip::tcp::socket socket, PeerNode &peerNode)
    : socket_(std::move(socket)), peerNode_(peerNode), closed_(false) {}

void Connection::start() {
  thread_ = std::thread(&Connection::receiveMessages, shared_from_this());
}

void Connection::receiveMessages() {
  try {
    while (!closed_) {
      readMessage();
    }
  } catch (std::exception &e) {
    std::cerr << "Error: " << e.what() << std::endl;
    close();
  }
}

void Connection::readMessage() {
  boost::asio::streambuf buffer;
  boost::asio::read_until(socket_, buffer, "\n");

  std::istream is(&buffer);
  std::string line;
  std::getline(is, line);

  json parsedMessage = json::parse(line);
  Message message = {parsedMessage["content"].get<std::string>(),
                     parsedMessage["author"].get<std::string>(),
                     std::chrono::system_clock::from_time_t(
                         parsedMessage["timestamp"].get<std::time_t>())};
  peerNode_.processIncomingMessage(message);
}

void Connection::propagate(const Message &message) {
  if (closed_)
    return;

  json jsonMessage = {{"content", message.getContent()},
                      {"author", message.getAuthor()},
                      {"timestamp", std::chrono::system_clock::to_time_t(
                                        message.getTimestamp())}};

  std::string serializedMessage = jsonMessage.dump() + "\n";
  boost::asio::write(socket_, boost::asio::buffer(serializedMessage));
}

void Connection::close() {
  if (closed_)
    return;
  closed_ = true;
  socket_.close();
  if (thread_.joinable())
    thread_.join();
}
