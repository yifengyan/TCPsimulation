lassign $argv TCP_SOURCE TCP2_SOURCE CBR_RATE QUEUE_LIMIT
#puts TCP_SOURCE
#puts CBR_RATE
#puts QUEUE_LIMIT

#Create a simulator object
set ns [new Simulator]

#Open the trace file (before you start the experiment!)

set tf [open Compare_Between$TCP_SOURCE-And-$TCP2_SOURCE-With_CBR_$CBR_RATE-Q-$QUEUE_LIMIT.tr w]
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
$ns duplex-link $n1 $n2 10Mb 10ms DropTail
$ns duplex-link $n5 $n2 10Mb 10ms DropTail
$ns duplex-link $n2 $n3 10Mb 10ms DropTail
$ns duplex-link $n3 $n4 10Mb 10ms DropTail
$ns duplex-link $n3 $n6 10Mb 10ms DropTail

#Setup the Network topology Orientation
#$ns duplex-link-op $n1 $n2 orient right-down
#$ns duplex-link-op $n5 $n2 orient right-up
#$ns duplex-link-op $n2 $n3 orient right
#$ns duplex-link-op $n3 $n4 orient right-up
#$ns duplex-link-op $n3 $n6 orient right-down

#Set Queue Size
$ns queue-limit $n1 $n2 $QUEUE_LIMIT
$ns queue-limit $n2 $n3 $QUEUE_LIMIT
$ns queue-limit $n3 $n4 $QUEUE_LIMIT
$ns queue-limit $n2 $n5 $QUEUE_LIMIT
$ns queue-limit $n3 $n6 $QUEUE_LIMIT

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

#TCP Source at n5 and TCP Sink at n6
set tcp2 [new Agent/TCP/$TCP2_SOURCE]
$tcp2 set class_ 2
$tcp2 set window_ 8000
$ns attach-agent $n5 $tcp2
set sink [new Agent/TCPSink]
$ns attach-agent $n6 $sink

#Connect the TCP source and sink
$ns connect $tcp2 $sink
 
#Setup a FTP Agent over the TCP connection
set ftp2 [new Application/FTP]
$ftp2 attach-agent $tcp2
$ftp2 set type_ FTP

#UDP Source at n2 and Sink at n3
set udp [new Agent/UDP]
$ns attach-agent $n2 $udp
set null [new Agent/Null]

#Connect the traffic source and sink
$ns attach-agent $n3 $null
$ns connect $udp $null

#Setup a CBR over UDP connection
set cbr [new Application/Traffic/CBR]
$cbr attach-agent $udp
$cbr set type_ CBR
$cbr set packet_size_ 1000
$cbr set rate_ $CBR_RATE
$cbr set random_ false

#Schedule events for the CBR and FTP agents
$ns at 2.0 "$ftp start"
$ns at 40.0 "$ftp stop"

$ns at 10.0 "$cbr start"
$ns at 40.0 "$cbr stop"

$ns at 2.0 "$ftp2 start" 
$ns at 40.0 "$ftp2 stop"

#Call the finish procedure 
$ns at 70.0 "finish"

#Run the simulation
$ns run
