all: doc

doc:
	mkdir -p dist/latest/
	cd ..; javadoc -d doc/dist -cp src -subpackages org

github: doc
	ghp-import -m "Generate documentation" -b gh-pages dist/
	git push origin gh-pages
