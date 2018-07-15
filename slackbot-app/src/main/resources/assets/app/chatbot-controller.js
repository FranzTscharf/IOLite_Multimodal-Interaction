(function() {
    'use strict';
    var exampleApp = angular.module('exampleApp');
    /**
     * This controller calls 'rooms' and 'devices' request handlers which are registered in the ExampleApp.java.
     */
    exampleApp.controller('chatbot-controller', [ '$scope', '$http', function($scope, $http) {
        $scope.ApiKeyValueLoadedFromStorageAPI = null;
        ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
            success : function(storageAPI) {
                storageAPI.action({
                    request : new ActionRequest(/* requestIdentifier */null, /* modelIdentifier */null, /* objectQuery */ ".", /* actionName */ "loadString", /* parameters */ [ new ValueParameter("apikey") ]),
                    success : function(value, storageAPI, request) {
                        $scope.ApiKeyValueLoadedFromStorageAPI = value;
                        document.getElementById("inputAPIKEY").value = value;
                    },
                    error : function(storageAPI, responseRequestID, responseErrorCode, responseError) {
                        console.error("Action " + responseRequestID + " '" + objectQuery + "' failed due to " + responseErrorCode + ": " + responseError);
                    }
                });
            }
        });
        $scope.ApiKeyValueSaveToStorageAPI = function() {
            //call the stoage API to save the APIKEY
            ModelAPIProfiles.get(ModelAPIProfiles.storageId, {
                success : function(storageAPI) {
                    storageAPI.action({
                        request : new ActionRequest(/* requestIdentifier */null, /* modelIdentifier */null, /* objectQuery */ ".", /* actionName */ "saveString", /* parameters */ [ new ValueParameter("apikey") , new ValueParameter($scope.ApiKeyChanged)]),
                        success : function(value, storageAPI, request) {
                            console.log("ApiKeyChanged");
                            console.log($scope.ApiKeyChanged);
                            var element = document.getElementById("saveAPIKEYButton");
                            element.className = "btn btn-success";
                            element.innerHTML = "Save Changes <i class='fas fa-check'></i>";
                        },
                        error : function(storageAPI, responseRequestID, responseErrorCode, responseError) {
                            console.error("Action " + responseRequestID + " '" + objectQuery + "' failed due to " + responseErrorCode + ": " + responseError);
                        }
                    });
                }
            });
        };
        $scope.sendTestMessage = function() {
            //call the stoage API to save the APIKEY
            $http.post('credentialCheck', { 'username' : $scope.slackUsername }).then(function onSuccess(response) {
                var element = document.getElementById("sendTestMessageButton");
                element.className = "btn btn-success";
                element.innerHTML = "Check-Credentials <i class='fas fa-check'></i>";
                console.log("send test message");
                console.log(response);
            }, function onFailure(response) {
                $('#configWrong').modal('show');
                console.log(response);
                console.log("can't connect to slack");
            });
        };
        $scope.startSlackBot = function() {
            if(document.getElementById("startBotButton").className == "btn btn-success") {
                $('#serverStartedAlready').modal('show');
            }else{
                $scope.ApiKeyValueSaveToStorageAPI();
                //call the stoage API to save the APIKEY
                $http.get('startSlackBot').then(function onSuccess(response) {
                    var element = document.getElementById("startBotButton");
                    element.className = "btn btn-success";
                    element.innerHTML = "Start SlackBot <i class='fas fa-check'></i>";
                    console.log("start SlackBot");
                    console.log(response);
                }, function onFailure(response) {
                $('#configWrong').modal('show');
                console.log(response);
                console.log("can't connect to slack");
            });
            }
        };

    } ]);
})();