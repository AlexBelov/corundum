mas = []
mas = read_file("lab4.txt")

length = len(mas)

for(i = 1; i < length - 1; i+=1)
  for(j = 0; j < length - i; j+=1)
    if(mas[j] > mas[j+1])
      buf = mas[j]
      mas[j] = mas[j+1]
      mas[j+1] = buf
    end
  end
end

for(i = 0; i < length; i+=1)
  p mas[i]
end

write_file("lab4_sorted.txt", mas)
