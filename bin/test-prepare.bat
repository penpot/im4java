@echo off
@rem --------------------------------------------------------------------------
@rem
@rem Prepare im4java-tests on a Windows-platform without make-support
@rem
@rem $Author: bablokb $
@rem $Revision: 1.1 $
@rem
@rem License: GPL2
@rem --------------------------------------------------------------------------

if exist images rmdir /S /Q images
xcopy /E /I images.src images
copy images\tulip2.jpg images\"tulip 2.jpg"
