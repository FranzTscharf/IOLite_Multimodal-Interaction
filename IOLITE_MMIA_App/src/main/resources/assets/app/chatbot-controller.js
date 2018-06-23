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
            $http.get('TestClass').then(function onSuccess(response) {
                console.log("send test message");
                console.log(response);
            }, function onFailure(response) {
                console.log(response);
                console.log("can't get rooms");
            });
        };

    } ]);
})();