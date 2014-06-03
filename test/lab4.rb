file_name = ""
file_name = gets()

mas = []
mas = read_file file_name
mas = sort mas
puts_mas mas
write_file "test/lab4_sorted.txt", mas

def puts_mas mas
  length = len(mas)
  for i = 0; i < length; i+=1
    p mas[i]
  end
end
