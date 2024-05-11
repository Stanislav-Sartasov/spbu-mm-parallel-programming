#include "TerminalRenderer.h"
#include <chrono>
#include <iomanip>
#include <iostream>

void TerminalRenderer::render(const MessageLog &messageLog) {
  std::cout << "------------------------ Chat History ------------------------"
            << std::endl;
  for (const auto &message : messageLog) {
    auto time = std::chrono::system_clock::to_time_t(message.getTimestamp());
    std::tm tm = *std::localtime(&time);
    std::cout << "[" << std::put_time(&tm, "%d.%m %H:%M:%S") << "] "
              << message.getAuthor() << ": " << message.getContent()
              << std::endl;
  }
  std::cout << "-----------------------------------------------------------------"
      << std::endl
      << std::endl;
}
