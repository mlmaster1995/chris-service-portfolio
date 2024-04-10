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
{{- define "simple-rest-crud-service.chart" -}}
    {{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}


{{/*
Common labels across all components
*/}}
{{- define "simple-rest-crud-service.labels" -}}
helm.sh/chart: {{ include "simple-rest-crud-service.chart" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/app-version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{ include "simple-rest-crud-service.selectorLabels" . }}
{{- end }}
{{/*
Selector labels
*/}}
{{- define "simple-rest-crud-service.selectorLabels" -}}
app.kubernetes.io/name: {{ .Chart.Name }}
app.kubernetes.io/part-of: {{ .Release.Name}}
app.kubernetes.io/env: {{ .Values.global.deploy.env }}
{{- end }}

{{/*
Service account name
*/}}
{{- define "simple-rest-crud-service.global.serviceAccountName" -}}
    {{- if .Values.global.serviceAccount.enabled }}
        {{- .Values.global.serviceAccount.name }}
    {{- else }}
        {{- "default" }}
    {{- end }}
{{- end }}

{{/*
Create a default fully qualified app name (chart name)
We truncate at 63 chars, because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "simple-rest-crud-service.fullname" -}}
    {{- if .Values.fullnameOverride }}
        {{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
    {{- else }}
        {{- .Chart.Name | trunc 63 | trimSuffix "-" }}
    {{- end }}
{{- end }}