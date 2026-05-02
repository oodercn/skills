$file1 = 'e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\AgentEngineImpl.java'
$file2 = 'e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\engine\WorkflowClientServiceImpl.java'

foreach ($f in @($file1, $file2)) {
    $bytes = [System.IO.File]::ReadAllBytes($f)
    if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        $newBytes = New-Object byte[] ($bytes.Length - 3)
        [Array]::Copy($bytes, 3, $newBytes, 0, $bytes.Length - 3)
        [System.IO.File]::WriteAllBytes($f, $newBytes)
        Write-Host "Removed BOM from $f"
    } else {
        Write-Host "No BOM in $f"
    }
}
