$global = 42
not_global = $global

# mas = []
# mas = read_file("lab4.txt")

# length = len(mas)

# for(i = 1; i < length - 1; i+=1)
#   for(j = 0; j < length - i; j+=1)
#     if(mas[j] > mas[j+1])
#       buf = mas[j]
#       mas[j] = mas[j+1]
#       mas[j+1] = buf
#     end
#   end
# end

# mas = qsort mas, 0, length-1

# for(i = 0; i < length; i+=1)
#   p mas[i]
# end

# write_file("lab4_sorted.txt", mas)

# def qsort mas, first, last
#   i = first
#   j = last
#   x = mas[(first + last) / 2]
#   while i < j
#     while mas[i] < x
#       i+=1
#     end
#     while mas[j] < x
#       j-=1
#     end
#     if i<=j
#       if i<j
#         buf = mas[j]
#         mas[j] = mas[i]
#         mas[i] = buf
#       end
#       i+=1
#       j-=1
#     end
#   end
#   if i<last
#     qsort mas, i, last
#   end
#   if first<j
#     qsort mas, first, j
#   end
#   return mas
# end

# def partition array, start, end_index
#   marker = start
#   for (i = start; i <= end_index; i+=1) 
#     if ( array[i] <= array[end_index] ) 
#       temp = array[marker]
#       array[marker] = array[i]
#       array[i] = temp
#       marker += 1
#     end
#   end
#   return marker - 1;
# end

# def quicksort array, start, end_index
#   if start >= end_index 
#    return 0
#   end
#   pivot = partition (array, start, end_index);
#   quicksort (array, start, pivot-1);
#   quicksort (array, pivot+1, end_index);
# end
