{{/*
Expand the name of the chart.
*/}}
{{- define "quarkus-app.name" -}}
quarkus-app
{{- end -}}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "quarkus-app.fullname" -}}
{{ include "quarkus-app.name" . }}-{{ .Release.Name }}
{{- end -}}
