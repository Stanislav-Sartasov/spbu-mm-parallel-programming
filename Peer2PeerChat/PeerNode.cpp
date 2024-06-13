#include "PeerNode.h"
#include "Connection.h"

PeerNode::PeerNode(const std::string &username, int port)
    : username_(username), port_(port),
      acceptor_(ioContext_, boost::asio::ip::tcp::endpoint(
                                boost::asio::ip::tcp::v4(), port)) {}

void PeerNode::setRenderer(TerminalRenderer *renderer) { renderer_ = renderer; }

void PeerNode::start() {
  acceptConnections();
  ioContext_.run();
}

void PeerNode::acceptConnections() {
  auto connection = std::make_shared<Connection>(
      boost::asio::ip::tcp::socket(ioContext_), *this);
  acceptor_.async_accept(connection->socket_,
                         std::bind(&PeerNode::handleAccept, this, connection,
                                   std::placeholders::_1));
}

void PeerNode::handleAccept(std::shared_ptr<Connection> connection,
                            const boost::system::error_code &error) {
  if (!error) {
    {
      std::lock_guard<std::mutex> lock(mutex_);
      connections_.push_back(connection);
    }
    connection->start();
    acceptConnections();
  }
}

void PeerNode::connect(const std::string &host, int port) {
  auto connection = std::make_shared<Connection>(
      boost::asio::ip::tcp::socket(ioContext_), *this);
  connection->socket_.async_connect(
      boost::asio::ip::tcp::endpoint(
          boost::asio::ip::address::from_string(host), port),
      [this, connection](const boost::system::error_code &error) {
        if (!error) {
          {
            std::lock_guard<std::mutex> lock(mutex_);
            connections_.push_back(connection);
          }
          connection->start();
        }
      });
}

void PeerNode::shutdown() {
  ioContext_.stop();
  for (auto &connection : connections_) {
    connection->close();
  }
}

void PeerNode::sendMessage(const std::string &message) {
  Message msg(message, username_);
  {
    std::lock_guard<std::mutex> lock(mutex_);
    messageLog_.addMessage(msg);
    sentMessageContents_.insert(msg.getContent()); // Track sent message content
  }
  renderChatLog();
  propagateMessage(msg);
}

void PeerNode::processIncomingMessage(const Message &message) {
  std::lock_guard<std::mutex> lock(mutex_);
  // Check if the message content is already sent by this peer
  if (!messageLog_.contains(message) &&
      sentMessageContents_.find(message.getContent()) ==
          sentMessageContents_.end()) {
    messageLog_.addMessage(message);
    renderChatLog();
    propagateMessage(message);
  }
}

void PeerNode::propagateMessage(const Message &message) {
  for (const auto &connection : connections_) {
    connection->propagate(message);
  }
}

void PeerNode::renderChatLog() {
  if (renderer_) {
    renderer_->render(messageLog_);
  }
}
