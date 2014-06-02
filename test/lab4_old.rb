# a = []
# a[1] = 456

# $global = a[1]
# not_global = $global
# puts not_global
# not_global = 123
# $global = not_global
# a = $global
# puts a

mas = []
mas = read_file("lab4.txt")

length = len(mas)

# for(i = 1; i < length - 1; i+=1)
#   for(j = 0; j < length - i; j+=1)
#     if(mas[j] > mas[j+1])
#       buf = mas[j]
#       mas[j] = mas[j+1]
#       mas[j+1] = buf
#     end
#   end
# end

#puts_mas mas
puts "========================"

$mas = mas
#qsort 0, length-1
#quicksort 0, length-1
#mas = bubble mas, length
#mas = shell mas, length
$mas = mas
mas = sort mas

def puts_mas mas
  length = len(mas)
  for(i = 0; i < length; i+=1)
    p mas[i]
  end
end

puts_mas mas

# write_file("lab4_sorted.txt", mas)

# def shell mas, N
#   for d=N/2; d > 0; d/=2
#     for i=d; i < N; i+=1
#       for j = i; j>=d && mas[j - d] > mas[j]; j -= d
#         buf = mas[i]
#         mas[i] = mas[j]
#         mas[j] = buf
#       end
#     end
#   end
# end

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

def qsort first, last
  mas = $mas
  i = first
  j = last

  if j>i
    x = mas[(first + last) / 2]
    while i <= j
      while i < last && mas[i] < x
        i+=1
      end
      while j > first && mas[j] > x
        j-=1
      end
      if (i<=j)
        buf = mas[i]
        mas[i] = mas[j]
        mas[j] = buf
        i+=1
        j-=1
      end
    end
    $mas = mas
    if i < last
      qsort i, last
    end
    if first < j
      qsort first, j
    end 
  end
end

# def partition start, end_index
#   array = $mas
#   marker = start
#   for (i = start; i <= end_index; i+=1) 
#     if ( array[i] <= array[end_index] ) 
#       temp = array[marker]
#       array[marker] = array[i]
#       array[i] = temp
#       marker += 1
#     end
#   end
#   $mas = array
#   return (marker - 1)
# end

# def quicksort start, end_index
#   if start >= end_index 
#    return 0
#   end
#   pivot = partition (start, end_index)
#   quicksort (start, pivot-1)
#   quicksort (pivot+1, end_index)
# end

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
