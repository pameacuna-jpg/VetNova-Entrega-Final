# Lista de rutas a los reportes de JaCoCo
$reportes = @(
    "D:\Respaldos\clases\U-Duoc\Semestre 3\Desarrollo Fullstack 1\Junio\VetNova-Entrega-Final\gabriel-martinez-vetnova-microservicios-main\clientes\target\site\jacoco\index.html",
    "D:\Respaldos\clases\U-Duoc\Semestre 3\Desarrollo Fullstack 1\Junio\VetNova-Entrega-Final\gabriel-martinez-vetnova-microservicios-main\mascotas\target\site\jacoco\index.html",
    "D:\Respaldos\clases\U-Duoc\Semestre 3\Desarrollo Fullstack 1\Junio\VetNova-Entrega-Final\gabriel-martinez-vetnova-microservicios-main\usuarios\target\site\jacoco\index.html",
    "D:\Respaldos\clases\U-Duoc\Semestre 3\Desarrollo Fullstack 1\Junio\VetNova-Entrega-Final\Microservicios_Pamela\ms-inventario\ms-inventario\target\site\jacoco\index.html",
    "D:\Respaldos\clases\U-Duoc\Semestre 3\Desarrollo Fullstack 1\Junio\VetNova-Entrega-Final\Microservicios_Pamela\ms-notificaciones\ms-notificaciones\target\site\jacoco\index.html",
    "D:\Respaldos\clases\U-Duoc\Semestre 3\Desarrollo Fullstack 1\Junio\VetNova-Entrega-Final\Microservicios_Pamela\ms-sucursales-administracion\ms-sucursales-administracion\target\site\jacoco\index.html",
    "D:\Respaldos\clases\U-Duoc\Semestre 3\Desarrollo Fullstack 1\Junio\VetNova-Entrega-Final\Prueba-2-Castro-main\Castro\agenda\agenda\target\site\jacoco\index.html",
    "D:\Respaldos\clases\U-Duoc\Semestre 3\Desarrollo Fullstack 1\Junio\VetNova-Entrega-Final\Prueba-2-Castro-main\Castro\atencionclinica\atencionclinica\target\site\jacoco\index.html",
    "D:\Respaldos\clases\U-Duoc\Semestre 3\Desarrollo Fullstack 1\Junio\VetNova-Entrega-Final\Prueba-2-Castro-main\Castro\auth\auth\target\site\jacoco\index.html",
    "D:\Respaldos\clases\U-Duoc\Semestre 3\Desarrollo Fullstack 1\Junio\VetNova-Entrega-Final\Prueba-2-Castro-main\Castro\ventas\ventas\target\site\jacoco\index.html"
)

# Recorrer la lista y abrir los que existan
foreach ($ruta in $reportes) {
    if (Test-Path $ruta) {
        Write-Host "Abriendo: $ruta"
        Start-Process $ruta
    } else {
        Write-Warning "No se encontró el archivo: $ruta (¿ejecutaste mvn verify?)"
    }
}