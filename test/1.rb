require "2.rb"

mas = []
mas = read_file "lab4.txt"
mas = sort mas
puts_mas mas
write_file "lab4_sorted.txt", mas
