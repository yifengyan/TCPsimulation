lassign $argv TCP_SOURCE QUEUE
#puts TCP_SOURCE
#puts QUEUE

#Create a simulator object
set ns [new Simulator]

#Open the trace file (before you start the experiment!)

set tf [open Experiment3-$TCP_SOURCE-With-Q-$QUEUE.tr w]
$ns trace-all $tf

#Define a 'finish' procedure
proc finish {} {
        global ns tf
        $ns flush-trace
        close $tf
        exit 0
}

#if { $TCP_SOURCE != "TCP" } {
#	set TCP_SOURCE TCP/$TCP_SOURCE
#}

#Setup the six nodes for the network topology
set n1 [$ns node]
set n2 [$ns node]
set n3 [$ns node]
set n4 [$ns node]
set n5 [$ns node]
set n6 [$ns node]

#Create links between the nodes
$ns duplex-link $n1 $n2 10Mb 10ms $QUEUE
$ns duplex-link $n5 $n2 10Mb 10ms $QUEUE
$ns duplex-link $n2 $n3 10Mb 10ms $QUEUE
$ns duplex-link $n3 $n4 10Mb 10ms $QUEUE
$ns duplex-link $n3 $n6 10Mb 10ms $QUEUE

#Setup the Network topology Orientation
#$ns duplex-link-op $n1 $n2 orient right-down
#$ns duplex-link-op $n5 $n2 orient right-up
#$ns duplex-link-op $n2 $n3 orient right
#$ns duplex-link-op $n3 $n4 orient right-up
#$ns duplex-link-op $n3 $n6 orient right-down

#Set Queue Size
$ns queue-limit $n1 $n2 20
$ns queue-limit $n5 $n2 20
$ns queue-limit $n2 $n3 20
$ns queue-limit $n3 $n4 20
$ns queue-limit $n3 $n6 20

#TCP Source at n1 and TCP Sink at n4
set tcp [new Agent/TCP/$TCP_SOURCE]
$tcp set class_ 2
$tcp set window_ 10000
$ns attach-agent $n1 $tcp
set sink [new Agent/TCPSink]
$ns attach-agent $n4 $sink

#Connect the TCP source and sink
$ns connect $tcp $sink
 
#Setup a FTP Agent over the TCP connection
set ftp [new Application/FTP]
$ftp attach-agent $tcp
$ftp set type_ FTP

#UDP Source at n5 and Sink at n6
set udp [new Agent/UDP]
$ns attach-agent $n5 $udp
set null [new Agent/Null]

#Connect the traffic source and sink
$ns attach-agent $n6 $null
$ns connect $udp $null

#Setup a CBR over UDP connection with 8 Mbps
set cbr [new Application/Traffic/CBR]
$cbr attach-agent $udp
$cbr set type_ CBR
$cbr set packet_size_ 1000
$cbr set rate_ 8000000
$cbr set random_ false

#Schedule events for the CBR and FTP agents
$ns at 6.0 "$ftp start"
$ns at 60.0 "$ftp stop"

$ns at 15.0 "$cbr start"
$ns at 75.0 "$cbr stop"

#Call the finish procedure 
$ns at 80.0 "finish"

#Run the simulation
$ns run
