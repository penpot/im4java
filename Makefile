# -----------------------------------------------------------------------------
# Makefile for im4java
#
# $Author: bablokb $
# $Revision: 1.45 $
#
# License: GPL2 (see COPYING)
# -----------------------------------------------------------------------------

.PHONY: all src test test-prepare clean dist-clean \
        compile compile-lib compile-contrib \
        jar jar-lib jar-contrib jar-1.5 jar-contrib-1.5 \
        forrest api-doc doc-clean \
        show-news update-changelog \
        srcdist bindist predist srcarch binarch postdist \
        upload-files update-web \
        inc-dist-major inc-dist-minor inc-dist-pl

export 
include version.inc
VERSION  = $(DIST_MAJOR).$(DIST_MINOR).$(DIST_PL)

DIST       := im4java
DIST_NAME  := $(DIST)-$(VERSION)
DIST_DIR   := release/$(DIST_NAME)
DIST_STUFF := $(wildcard README* NEWS TODO INSTALL ChangeLog COPYING*) Makefile \
	      version.inc bin input doc-src images.src src contrib

SF_DIR := /home/frs/project/i/im/im4java

JAVA_PACKAGE=org.im4java.core
JAVA_TEST_CLASS=org.im4java.test.Test

PREFIX    = /usr/local
BINDIR    = $(PREFIX)/bin
ETCDIR    = /etc
SBINDIR   = $(PREFIX)/sbin
MANDIR    = $(PREFIX)/share/man
MAN1DIR   = $(MANDIR)/man1
MAN5DIR   = $(MANDIR)/man5
SHAREDIR  = $(PREFIX)/share/$(DIST)

# targets ---------------------------------------------------------------------

default:
	@echo -e "\nmain targets:\n"
	@echo -e "\tall:              recreates source and jar from scratch"
	@echo -e "\tsrc:              create java-sources from interface-definitions"
	@echo -e "\tcompile:          compile source-code"
	@echo -e "\tcompile-contrib:  compile contrib-source-code\n"
	@echo -e "\tjar:              create $(DIST_NAME).jar"
	@echo -e "\tjar-contrib:      create $(DIST_NAME)-contrib.jar\n"
	@echo -e "\ttest:             run test-suite"
	@echo -e "\ttest-prepare:     prepare tests\n"
	@echo -e "\tdoc:              create documentation"
	@echo -e "\tapi-doc:          create API-documentation"
	@echo -e "\tforrest:          create HTML-documentation"
	@echo -e "\tdoc-clean:        cleanup generated documentation\n"
	@echo -e "\tshow-news:        list of changes since last release"
	@echo -e "\tupdate-changelog: update ChangeLog for next release\n"
	@echo -e "\tsrcdist:          create source-distribution"
	@echo -e "\tbindist:          create binary-distribution (also includes source)\n"
	@echo -e "\tclean:            cleanup after compile and test"
	@echo -e "\tdist-clean:       complete cleanup"
	@echo -e ""

all: clean src jar

# targets (compilation) -------------------------------------------------------

# prevent excessive regeneration of source
src: src/org/im4java/core/IMOps.java
src/org/im4java/core/IMOps.java:
	bin/mk-im4java -p $(JAVA_PACKAGE)

compile: compile-lib

TARGET=11
compile-lib: src
	rm -fr build/*
	mkdir -p build
	javac -target $(TARGET) -d build/  `find src -name "*.java"`

compile-contrib: compile-lib
	rm -fr build.contrib/*
	mkdir -p build.contrib
	javac -target $(TARGET) -d build.contrib/ \
                              -cp build `find contrib -name "*.java"`

jar: jar-lib

jar-lib: compile-lib
	jar cmf input/manifest.mf $(DIST_NAME).jar -C build/ .

jar-1.5:
	$(MAKE) jar TARGET=1.5 DIST_NAME=$(DIST_NAME)-1.5

jar-contrib: compile-contrib
	jar cmf input/manifest.mf $(DIST_NAME)-contrib.jar -C build.contrib/ .

jar-contrib-1.5:
	$(MAKE) jar-contrib TARGET=1.5 DIST_NAME=$(DIST_NAME)-1.5


# targets (test) --------------------------------------------------------------

test-prepare:
	bin/test-prepare

TESTS=all
test: test-prepare
	java $(JAVA_OPTS) -cp build $(JAVA_TEST_CLASS) $(TESTS)

# targets (documentation) -----------------------------------------------------

DOC_SRC_DIR=./doc-src
DOC_DIR=./doc

NAME      = The im4java Library
HOMEPAGE  = http://im4java.sourceforge.net/
COPYRIGHT = Released under the LGPL, (c) Bernhard Bablok 2008-2010
WTITLE    = "$(NAME)"
DTITLE    = "$(NAME), Version $(VERSION)"
DBOTTOM   = "$(COPYRIGHT)<br>Homepage: <a href="$(HOMEPAGE)">$(HOMEPAGE)</a>"
DHEADER   = "<strong>$(NAME), Version $(VERSION)</strong>"
DFOOTER   = "<strong>$(NAME), Version $(VERSION)</strong>"

doc: api-doc forrest

forrest: doc/index.html
doc/index.html:
	cd $(DOC_SRC_DIR); \
	forrest && \
	cp -au build/site/* ../$(DOC_DIR)

api-doc: src
	mkdir -p $(DOC_DIR)/api
	javadoc -sourcepath src -d $(DOC_DIR)/api -windowtitle $(WTITLE) \
                -doctitle $(DTITLE) -footer $(DFOOTER) -header $(DHEADER) \
                -bottom $(DBOTTOM) \
                -version -author -subpackages org.im4java

doc-clean:
	rm -fr $(DOC_DIR)/* $(DOC_SRC_DIR)/build/*

# targets (cleanup and distribution) ------------------------------------------

clean:
	rm -fr $(DIST)-*.tar.bz2 build/* build.contrib/* $(DIST_DIR) \
                    images $(DIST_NAME)*.jar

dist-clean: clean doc-clean
	rm -fr src/$(subst .,/,$(JAVA_PACKAGE))/*Ops.java

show-news:
	cvs2cl.pl --group-within-date --no-times --accum --stdout > ChangeLog.new
	diff -ubB ChangeLog ChangeLog.new | less
	rm -f ChangeLog.new

update-changelog:
	cvs2cl.pl --group-within-date --no-times --accum
	rm -f ChangeLog.bak
	cvs commit -m"upated ChangeLog for version $(VERSION)" ChangeLog

srcdist: predist srcarch postdist

bindist: predist binarch postdist

predist:
	tar -cpzf $(DIST_NAME).tgz --exclude "CVS" \
           --exclude ".cvsignore" --exclude ".project" \
           --exclude ".classpath" --exclude ".settings" $(DIST_STUFF)
	rm -fr $(DIST_NAME)
	mkdir $(DIST_NAME)
	mkdir -p $(DIST_DIR)

srcarch:
	rm -f $(DIST_DIR)/$(DIST_NAME)-src.tar.bz2
	(cd $(DIST_NAME); tar -xpzf ../$(DIST_NAME).tgz; \
         $(MAKE) dist-clean src \
        )
	tar -cpjf $(DIST_DIR)/$(DIST_NAME)-src.tar.bz2 $(DIST_NAME)

binarch:
	rm -f $(DIST_DIR)/$(DIST_NAME)-bin.tar.bz2
	(cd $(DIST_NAME); tar -xpzf ../$(DIST_NAME).tgz; \
         $(MAKE) dist-clean src jar jar-1.5 doc; \
         rm -fr Makefile version.inc build src contrib doc-src input \
        )
	tar -cpjf  $(DIST_DIR)/$(DIST_NAME)-bin.tar.bz2 $(DIST_NAME)

postdist:
	rm -fr $(DIST_NAME) $(DIST_NAME).tgz


# targets (release management)  -----------------------------------------------

upload-files:
	rsync -avP -e ssh $(DIST_DIR) bablokb,im4java@frs.sourceforge.net:$(SF_DIR)

update-web: doc-clean doc
	rsync -avP -e ssh doc/  bablokb,im4java@web.sourceforge.net:htdocs/

# targets (version management)  -----------------------------------------------

commit-version:
	cvs commit -m"upgraded version to $(VERSION)" version.inc

inc-dist-major:
	@echo DIST_MAJOR=$$[$(DIST_MAJOR)+1] > version.inc
	@echo DIST_MINOR=0 >> version.inc
	@echo DIST_PL=0 >> version.inc
	$(MAKE) commit-version
	@cat version.inc

inc-dist-minor:
	@echo DIST_MAJOR=$(DIST_MAJOR) > version.inc
	@echo DIST_MINOR=$$[$(DIST_MINOR)+1] >> version.inc
	@echo DIST_PL=0 >> version.inc
	$(MAKE) commit-version
	@cat version.inc

inc-dist-pl:
	@echo DIST_MAJOR=$(DIST_MAJOR) > version.inc
	@echo DIST_MINOR=$(DIST_MINOR) >> version.inc
	@echo DIST_PL=$$[$(DIST_PL)+1] >> version.inc
	$(MAKE) commit-version
	@cat version.inc
