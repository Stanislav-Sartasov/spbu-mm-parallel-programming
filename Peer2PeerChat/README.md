# Peer-to-Peer Chat App

## Dependencies

- g++
- make
- boost (for Boost.Asio)
- nlohmann/json (for JSON parsing)

Install dependencies on Ubuntu:

```bash
sudo apt-get install g++ make libboost-all-dev
```

Download the nlohmann/json single header file and place it in the `include` directory.

## Building the Application

To build the chat application, run the following command in the terminal:

```bash
make
```

This will produce an executable named `chat`.

## Usage

To start a chat peer:

```bash
./chat
```

Follow the prompts to enter your username and port number.

## P.S.

The program was tested using `-fsanitize=thread`, `-fsanitize=address` and `-fsanitize=undefined`.
