package space.astro.bot.managers.vc

import space.astro.bot.managers.vc.dto.VCOperationCTX
import space.astro.shared.core.models.database.InitialPosition
import space.astro.shared.core.models.database.TemporaryVCData

object VCNameManager {
    fun VCOperationCTX.performVCRename(newNameTemplate: String) {
        if (!temporaryVCData.canBeRenamed()) {
            return
        }

        performPositionUpdates(newNameTemplate)

        val newName = VariablesManager.computeVcNameForExisting(newNameTemplate, temporaryVCOwner, temporaryVC, temporaryVCData.incrementalPosition)
        
        performNameUpdates(newName)
    }
    
    fun VCOperationCTX.performVCNameRefresh() {
        if (!temporaryVCData.canBeRenamed()) {
            return
        }
        
        val nameTemplate = VariablesManager.getNameTemplateForRefresh(temporaryVCData, generatorData)
        
        performPositionUpdates(nameTemplate)
        
        val newName = VariablesManager.computeVcNameForExisting(nameTemplate, temporaryVCOwner, temporaryVC, temporaryVCData.incrementalPosition)
        performNameUpdates(newName)
    }
    
    
    /////////////////////////////////
    /// TEMPORARY VC DATA HELPERS ///
    /////////////////////////////////
    
    private fun TemporaryVCData.canBeRenamed(): Boolean {
        val currentTime = System.currentTimeMillis()

        if (lastNameChange == null || (currentTime - lastNameChange!!) > 600000)
            return true

        if (nameChanges < 2)
            return true

        return false
    }

    private fun TemporaryVCData.performRenameOperationsOnTemporaryVCData() {
        if (canBeRenamed()) {
            val currentTime = System.currentTimeMillis()

            if (lastNameChange == null || (currentTime - lastNameChange!!) > 600000) {
                lastNameChange = currentTime
                nameChanges = 1
            } else {
                lastNameChange = currentTime
                nameChanges++
            }
        }
    }
    
    /////////////////////
    /// OTHER HELPERS ///
    /////////////////////

    private fun VCOperationCTX.performPositionUpdates(
        nameTemplate: String
    ) {
        val requiresPositionalData = VariablesManager.doesTemplateRequireVCPositionalData(nameTemplate)

        if (requiresPositionalData) {
            val incrementalPosition = VCPositionManager.getIncrementalPosition(
                generatorId = generatorData.id,
                excludedVCId = null,
                temporaryVCs = temporaryVCsData
            )
            temporaryVCData.incrementalPosition = incrementalPosition

            VCPositionManager.getRawPosition(incrementalPosition, generatorData, generator)
                ?.also {  temporaryVCRawPosition ->
                    temporaryVCManager.setPosition(temporaryVCRawPosition)

                    markTemporaryVCManagerAsUpdated()

                    if (waitingRoomManager != null) {
                        when (generatorData.waitingPosition) {
                            InitialPosition.AFTER -> temporaryVCRawPosition + 1
                            InitialPosition.BEFORE -> temporaryVCRawPosition - 1
                            InitialPosition.BOTTOM -> null
                        }?.also {  waitingRoomRawPosition ->
                            waitingRoomManager.setPosition(waitingRoomRawPosition)
                            markWaitingRoomManagerAsUpdated()
                        }
                    }
                }
        }
    }
    
    private fun VCOperationCTX.performNameUpdates(
        name: String
    ) {
        if (temporaryVC.name == name) {
            return
        }

        temporaryVCData.performRenameOperationsOnTemporaryVCData()
        temporaryVCManager.setName(name)
        markTemporaryVCManagerAsUpdated()
    }
}