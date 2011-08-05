#!/usr/bin/python
#  Copyright (C) 2011 Gluster, Inc. <http://www.gluster.com>
#  This file is part of Gluster Management Gateway.
#

import os
import sys
p1 = os.path.abspath(os.path.dirname(sys.argv[0]))
p2 = "%s/common" % os.path.dirname(p1)
if not p1 in sys.path:
    sys.path.append(p1)
if not p2 in sys.path:
    sys.path.append(p2)
import Utils


cifsVolumeFile = "/opt/glustermg/etc/volumes.cifs"


def main():
    if len(sys.argv) < 2:
        sys.stderr.write("usage: %s VOLUME_NAME\n" % os.path.basename(sys.argv[0]))
        sys.exit(-1)

    volumeName = sys.argv[1]

    try:
        fp = open(cifsVolumeFile)
        content = fp.read()
        fp.close()
        for line in content.split():
            tokens = line.split(":")
            if tokens[0] == volumeName:
                print "\n".join(tokens[1:])
                sys.exit(0)
        # given volume is not configured for cifs export
        sys.exit(0)
    except IOError, e:
        Utils.log("failed to read file %s: %s" % (cifsVolumeFile, str(e)))
        sys.exit(2)


if __name__ == "__main__":
    main()