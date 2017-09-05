#! /bin/sh
# Copyright (c) 1995-2000 SuSE GmbH Nuernberg, Germany.
#
# Author: NekoKoneko (Konata Izumi) a@2kx.ru
#

TCBIN=/usr/sbin/tc
JNEKOIF=lan1

case "$1" in
    start)
	echo "Adding iptables rules..."
    
        modprobe xt_mark
        echo 1 > /proc/sys/net/ipv4/ip_forward
        cat /etc/sysconfig/iptables | iptables-restore

        echo "Adding tc rules..."
	modprobe ifb
	ip link set dev ifb0 up
	tc qdisc add dev $JNEKOIF ingress
	tc filter add dev $JNEKOIF parent ffff: protocol ip u32 match u32 0 0 action mirred egress redirect dev ifb0
	
	$TCBIN qdisc  del dev $JNEKOIF root
	$TCBIN qdisc  add dev $JNEKOIF root handle 1:0 htb default 10
	$TCBIN class  add dev $JNEKOIF parent 1:0 classid 1:1 htb rate 1000mbit ceil 1000mbit burst 64k
	$TCBIN class  add dev $JNEKOIF parent 1:1 classid 1:10 htb rate 128kbit burst 16k

	$TCBIN qdisc  del dev ifb0 root
	$TCBIN qdisc  add dev ifb0 root handle 1:0 htb default 10
	$TCBIN class  add dev ifb0 parent 1:0 classid 1:1 htb rate 1000mbit ceil 1000mbit burst 64k
	$TCBIN class  add dev ifb0 parent 1:1 classid 1:10 htb rate 128kbit burst 16k

##JNEKO_TC_QDISC_TAG##
##JNEKO_TC_FILTER_TAG##

	echo "Restarting dhcpd..."
	service dhcpd restart
	;;
    stop)

        ;;
    restart)
        $0 stop
        $0 start
        ;;
    *)
        echo "Usage: $0 {start|stop|restart}"
        exit 1
        ;;
esac

