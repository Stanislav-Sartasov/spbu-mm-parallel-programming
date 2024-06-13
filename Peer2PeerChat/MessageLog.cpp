#include "MessageLog.h"
#include <algorithm>

void MessageLog::addMessage(const Message &message) {
  std::lock_guard<std::mutex> lock(mutex_);
  auto it = std::lower_bound(messages_.begin(), messages_.end(), message,
                             [](const Message &a, const Message &b) {
                               return a.getTimestamp() < b.getTimestamp() ||
                                      (a.getTimestamp() == b.getTimestamp() &&
                                       a.getContent() < b.getContent());
                             });
  messages_.insert(it, message);
}

bool MessageLog::contains(const Message &message) const {
  std::lock_guard<std::mutex> lock(mutex_);
  return std::find(messages_.begin(), messages_.end(), message) !=
         messages_.end();
}

std::vector<Message>::const_iterator MessageLog::begin() const {
  return messages_.begin();
}

std::vector<Message>::const_iterator MessageLog::end() const {
  return messages_.end();
}
