#include <iostream>
#include <fstream>
#include <sstream>
#include <thread>
#include <string>
#include <vector>
#include <iterator>
#include <future>
#include <tbb/concurrent_hash_map.h>

int main(int argn, char* argv[])
{
    if(argn < 2)
    {
        std::cerr << "Невказано файл!" << std::endl;
        return 1;
    }
    std::ifstream file(argv[1]);
    if(!file.is_open())
    {
        std::cerr << "Помилка відкриття файлу!" << std::endl;
        return 1;
    }

    std::vector<std::string> words;

    std::copy(std::istream_iterator<std::string>(file), std::istream_iterator<std::string>(), std::back_inserter(words));

    file.close();

    tbb::concurrent_hash_map<std::string, int> hash_map;

    auto foo = [&](auto&& prom, std::vector<std::string> words)
    {
        for(auto&& word : words)
        {
            if(!hash_map.count(word))
            {
                hash_map.insert({word, 1});
            }
            else
            {
                tbb::concurrent_hash_map<std::string, int>::accessor acc;
                hash_map.find(acc, word);
                acc->second++;
            }
        }
        prom.set_value();
    };

    std::promise<void> prom1;
    std::promise<void> prom2;
    std::future<void> fut1 = prom1.get_future();
    std::future<void> fut2 = prom2.get_future();

    std::thread(foo, std::move(prom1), std::vector<std::string>{words.begin(), words.begin() + words.size() / 2}).detach();
    std::thread(foo, std::move(prom2), std::vector<std::string>{words.begin() + words.size() / 2, words.end()}).detach();

    fut1.wait();
    fut2.wait();

    for(auto&& word : hash_map)
    {
        std::cout << word.second << " " << word.first << std::endl;
    }

    return 0;
}