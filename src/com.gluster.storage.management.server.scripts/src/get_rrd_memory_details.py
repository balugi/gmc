#!/usr/bin/python
#  Copyright (C) 2010 Gluster, Inc. <http://www.gluster.com>
#  This file is part of Gluster Storage Platform.
#
#  Gluster Storage Platform is free software; you can redistribute it
#  and/or modify it under the terms of the GNU General Public License
#  as published by the Free Software Foundation; either version 3 of
#  the License, or (at your option) any later version.
#
#  Gluster Storage Platform is distributed in the hope that it will be
#  useful, but WITHOUT ANY WARRANTY; without even the implied warranty
#  of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program.  If not, see
#  <http://www.gnu.org/licenses/>.

# Input command: get_rrd_memory_details.py 1hour
# OUTPUT as bellow:
# <?xml version="1.0" encoding="ISO-8859-1"?>
#
# <xport>
#   <meta>
#     <start>1310455500</start>
#     <step>300</step>
#     <end>1310459100</end>
#     <rows>13</rows>
#     <columns>4</columns>
#     <legend>
#       <entry>memoryUsed</entry>
#       <entry>memoryFree</entry>
#       <entry>memoryCache</entry>
#       <entry>totalMemory</entry>
#     </legend>
#   </meta>
#   <data>
#     <row><t>1310455500</t><v>1.9181091707e+06</v><v>1.5819754974e+06</v><v>1.2528146351e+06</v><v>3.5000846681e+06</v></row>
#     <row><t>1310455800</t><v>1.9037555461e+06</v><v>1.5963191358e+06</v><v>1.2528521002e+06</v><v>3.5000746819e+06</v></row>
#     <row><t>1310456100</t><v>1.9038003766e+06</v><v>1.5963538435e+06</v><v>1.2529487374e+06</v><v>3.5001542201e+06</v></row>
#     <row><t>1310456400</t><v>1.9042611443e+06</v><v>1.5959639300e+06</v><v>1.2530286311e+06</v><v>3.5002250743e+06</v></row>
#     <row><t>1310456700</t><v>1.9044356924e+06</v><v>1.5957328399e+06</v><v>1.2530492795e+06</v><v>3.5001685323e+06</v></row>
#     <row><t>1310457000</t><v>1.9048233351e+06</v><v>1.5952823779e+06</v><v>1.2530540764e+06</v><v>3.5001057130e+06</v></row>
#     <row><t>1310457300</t><v>1.9047911068e+06</v><v>1.5952553868e+06</v><v>1.2530601913e+06</v><v>3.5000464936e+06</v></row>
#     <row><t>1310457600</t><v>1.9048929048e+06</v><v>1.5953391701e+06</v><v>1.2531675638e+06</v><v>3.5002320749e+06</v></row>
#     <row><t>1310457900</t><v>1.9051587666e+06</v><v>1.5947842070e+06</v><v>1.2531049438e+06</v><v>3.4999429736e+06</v></row>
#     <row><t>1310458200</t><v>1.9059319764e+06</v><v>1.5942797579e+06</v><v>1.2532148443e+06</v><v>3.5002117344e+06</v></row>
#     <row><t>1310458500</t><v>1.9058528445e+06</v><v>1.5941925515e+06</v><v>1.2531962561e+06</v><v>3.5000453961e+06</v></row>
#     <row><t>1310458800</t><v>NaN</v><v>NaN</v><v>NaN</v><v>NaN</v></row>
#     <row><t>1310459100</t><v>NaN</v><v>NaN</v><v>NaN</v><v>NaN</v></row>
#   </data>
# </xport>

import os
import sys
import syslog
from XmlHandler import ResponseXml
import Utils

def getMemData(period):
    memRrdFile = "/var/lib/rrd/mem.rrd"
    rs = ResponseXml()
    command = "rrdtool xport --start -%s \
                 DEF:free=%s:memfree:AVERAGE \
                 DEF:used=%s:memused:AVERAGE \
                 DEF:cache=%s:memcache:AVERAGE \
                 CDEF:total=used,free,+ \
                 XPORT:used:memoryUsed \
                 XPORT:free:memoryFree \
                 XPORT:cache:memoryCache \
                 XPORT:total:totalMemory" % (period, memRrdFile, memRrdFile, memRrdFile)

    rv = Utils.runCommand(command, output=True, root=True)
    message = Utils.stripEmptyLines(rv["Stdout"])
    if rv["Stderr"]:
        error = Utils.stripEmptyLines(rv["Stderr"])
        message += "Error: [%s]" % (error)
        Utils.log("failed to create RRD file for memory usages %s" % file)
        rs.appendTagRoute("status.code", rv["Status"])
        rs.appendTagRoute("status.message", message)
        return rs.toxml()
    return rv["Stdout"]

def main():
    if len(sys.argv) != 2:
        sys.stderr.write("usage: %s <period>\n" % os.path.basename(sys.argv[0]))
        sys.exit(-1)

    period = sys.argv[1]
    print getMemData(period)
    sys.exit(0)

if __name__ == "__main__":
    main()
