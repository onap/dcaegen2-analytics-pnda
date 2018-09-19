SERVER=https://knox.example.com:8443/gateway/pnda/deployment
APP=src/universal/sparkStreaming/PndaZTTApp/PndaZTTApp.jar
PACKAGE=pnda-ztt-app-0.0.3.tar.gz

app:		## Build the application jar
	sbt assembly

package:	## Build the deployable package
	sbt 'universal:packageZipTarball'

deploy:		## Deploy the package to PNDA cluster
	curl -k -u pnda:pnda -X PUT $(SERVER)/packages/$(PACKAGE) --data-binary @target/universal/$(PACKAGE) > /dev/null

list:		## Show the deployed packages
	curl $(SERVER)/packages

delete:		## Delete the deployed package
	curl -XDELETE $(SERVER)/packages/$(PACKAGE)

test/PndaZTTApp.jar:	$(APP) test/application.properties
	cp $< $@
	jar uvf $@ -C test application.properties

test:	test/PndaZTTApp.jar	## Run the assembly with spark-submit
	spark-submit \
		--driver-java-options "-Dlog4j.configuration=file://$(PWD)/test/log4j.testing.properties" \
		--class com.cisco.ztt.App \
		--master 'local[4]' --deploy-mode client \
		test/PndaZTTApp.jar

clean:		## Run sbt clean
	sbt clean
	rm -f $(APP)
	rm -f test/PndaZTTApp.jar

help:		## This help
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

.PHONY: help test
.DEFAULT_GOAL := help
