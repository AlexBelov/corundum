.sub p
  .param pmc str
  $P1 = new "String"
  $P1 = str
  print $P1
.end

.sub puts
  .param pmc str
  $P1 = new "String"
  $P1 = str
  say $P1
.end

.sub gets
  .local pmc stdin
  .local string str
  stdin = getstdin
  str = stdin.'readline'()
  $I0 = len(str)
  $I0 -= 1
  $P0 = split "", str
  $I1 = 0
  $P1 = new "ResizablePMCArray"
loop_truncate:
  $S1 = $P0[$I1]
  push $P1, $S1
  inc $I1
  if $I1 < $I0 goto loop_truncate
  $S2 = join "", $P1
  .return($S2)
.end

.sub len
  .param pmc array
  elements $I0, array
  .return($I0)
.end

.sub to_int
  .param pmc var
  $I0 = var
  .return($I0)
.end

.sub to_float
  .param pmc var
  $N0 = var
  .return($N0)
.end

.sub to_string
  .param pmc var
  $S0 = var
  .return($S0)
.end

.loadlib 'io_ops'

.sub read_file
  .param pmc file_name
  .local pmc array
  array = new "ResizablePMCArray"
  $S0 = file_name
  $P0 = open $S0, "r"
loop_file:
  $S0 = readline $P0
  if $S0 == "" goto end_loop
  push array, $S0
  if $P0 goto loop_file
end_loop:
  .return(array)
.end

.sub write_file
  .param pmc file_name
  .param pmc array
  $P1 = array
  $I1 = len($P1)
  $S0 = file_name
  $P0 = open $S0, "w"
  $I0 = 0
loop_file:
  $S0 = $P1[$I0]
  if $S0 == "\n" goto loop_file_1
  print $P0, $S0
loop_file_1:
  $I0 = $I0 + 1
  $S1 = $P1[$I0]
  if $I0 < $I1 goto loop_file
  close $P0
.end

.sub sort
  .param pmc mas
  mas.'sort'()
  .return(mas)
.end
