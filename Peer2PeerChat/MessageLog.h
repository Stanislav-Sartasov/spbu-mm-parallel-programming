#pragma once

#include "Message.h"
#include <mutex>
#include <vector>

class MessageLog {
public:
  void addMessage(const Message &message);
  bool contains(const Message &message) const;
  std::vector<Message>::const_iterator begin() const;
  std::vector<Message>::const_iterator end() const;

private:
  mutable std::mutex mutex_;
  std::vector<Message> messages_;
};
