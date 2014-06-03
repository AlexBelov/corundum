mas = []
mas = read_file("test/lab4_1.txt")

length = len(mas)

#mas = bubble mas, length
mas = shell mas, length
#mas = sort mas

def puts_mas mas
  length = len(mas)
  for(i = 0; i < length; i+=1)
    p mas[i]
  end
end

puts_mas mas

def shell mas, length
  step = length / 2
  while step > 0
    for i = 0; i < length-step; i+=1
      j = i
      while j>=0 && mas[j] > mas[j+step]
        buf = mas[j]
        mas[j] = mas[j+step]
        mas[j+step] = buf
        j-=1
      end
    end
    step /= 2
  end
  return mas
end

def bubble mas, length
  for(i = 1; i < length - 1; i+=1)
    for(j = 0; j < length - i; j+=1)
      if(mas[j] > mas[j+1])
        buf = mas[j]
        mas[j] = mas[j+1]
        mas[j+1] = buf
      end
    end
  end
  return mas
end
