#include "Message.h"

Message::Message(const std::string &content, const std::string &author)
    : content_(content), author_(author),
      timestamp_(std::chrono::system_clock::now()) {}

Message::Message(const std::string &content, const std::string &author,
                 std::chrono::system_clock::time_point timestamp)
    : content_(content), author_(author), timestamp_(timestamp) {}

const std::string &Message::getContent() const { return content_; }

const std::string &Message::getAuthor() const { return author_; }

const std::chrono::system_clock::time_point &Message::getTimestamp() const {
  return timestamp_;
}

bool Message::operator==(const Message &other) const {
  return content_ == other.content_ && author_ == other.author_ &&
         timestamp_ == other.timestamp_;
}
