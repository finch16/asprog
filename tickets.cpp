#include <iostream>
#include <thread>
#include <mutex>
#include <semaphore>
#include <list>

constexpr int quantity_tickets = 10;

class people
{
    std::counting_semaphore<quantity_tickets>& tickets;

public:
    people(std::counting_semaphore<quantity_tickets>& tickets) : tickets(tickets) {}

    void operator()(int i)
    {
        if(tickets.try_acquire())
        {
            std::cout << "Людина " << i << " купує квиток" << std::endl;
            std::this_thread::sleep_for(std::chrono::milliseconds(20));
        }
        else
        {
            std::cout << "Людина " << i << " не змогла купити квиток" << std::endl;
        }
    }
};

class service
{
    std::counting_semaphore<quantity_tickets> tickets;
    std::mutex mutex;
    bool is_available_hours;
    std::thread executor;
    std::thread clock;

    bool is_open()
    {
        std::unique_lock<std::mutex> lock(mutex);
        return is_available_hours;
    };

    void open()
    {
        std::unique_lock<std::mutex> lock(mutex);
        is_available_hours = true;
    };

    void close()
    {
        std::unique_lock<std::mutex> lock(mutex);
        is_available_hours = false;
    };

public:
    service() : tickets(quantity_tickets), is_available_hours(false), executor([&]()
    {
        std::list<std::thread> peoples;
        int i = 0;
        while(i < quantity_tickets + 2)
        {
            if(is_open())
            {
                peoples.push_back(std::thread(people{tickets}, i++));
            }
            else
            {
                std::cout << "Не час купівлі квитка" << std::endl;
            }
            std::this_thread::sleep_for(std::chrono::seconds(3));
        }

        std::cout << "Каса закрилася" << std::endl;

        for(auto& p : peoples)
        {
            p.join();
        }
    }), clock([&]()
    {
        for(;;)
        {
            std::this_thread::sleep_for(std::chrono::seconds(6));
            open();
            std::this_thread::sleep_for(std::chrono::seconds(18));
            close();
        }
    }) {clock.detach();}

    service(service& s) = delete;

    ~service()
    {
       executor.join();
    }
};

int main()
{
    service service;

    return 0;
}