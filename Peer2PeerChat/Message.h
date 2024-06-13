#pragma once

#include <chrono>
#include <string>

class Message {
public:
  Message(const std::string &content, const std::string &author);
  Message(const std::string &content, const std::string &author,
          std::chrono::system_clock::time_point timestamp);

  const std::string &getContent() const;
  const std::string &getAuthor() const;
  const std::chrono::system_clock::time_point &getTimestamp() const;

  bool operator==(const Message &other) const;

private:
  std::string content_;
  std::string author_;
  std::chrono::system_clock::time_point timestamp_;
};
