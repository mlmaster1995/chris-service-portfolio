{{/*
 MIT License                                                                   
                                                                               
 Copyright (c) 2024 Chris Yang                                                 
                                                                               
 Permission is hereby granted, free of charge, to any person obtaining a copy  
 of this software and associated documentation files (the "Software"), to deal 
 in the Software without restriction, including without limitation the rights  
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     
 copies of the Software, and to permit persons to whom the Software is         
 furnished to do so, subject to the following conditions:                      
                                                                               
 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.                               
                                                                               
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 SOFTWARE.                                                                     
*/}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "jenkins-cicd-test-service.chart" -}}
    {{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels across all components
*/}}
{{- define "jenkins-cicd-test-service.labels" -}}
helm.sh/chart: {{ include "jenkins-cicd-test-service.chart" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/app-version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{ include "jenkins-cicd-test-service.selectorLabels" . }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "jenkins-cicd-test-service.selectorLabels" -}}
app.kubernetes.io/name: {{ .Release.Name }}
app.kubernetes.io/env: {{ .Values.global.deploy.env }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "jenkins-cicd-test-service.serviceAccountName" -}}
    {{- if .Values.k8s.serviceaccount }}
        {{- default (include "jenkins-cicd-test-service.fullname" .) .Values.serviceAccount.name }}
    {{- else }}
        {{- default "default" .Values.serviceAccount.name }}
    {{- end }}
{{- end }}

{{/*
Create a default fully qualified app name (chart name)
We truncate at 63 chars, because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "jenkins-cicd-test-service.fullname" -}}
    {{- if .Values.fullnameOverride }}
        {{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
    {{- else }}
        {{- .Chart.Name | trunc 63 | trimSuffix "-" }}
    {{- end }}
{{- end }}