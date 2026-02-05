import type { TurboModule } from 'react-native'
import { TurboModuleRegistry } from 'react-native'
import { Source } from './index'

export interface Spec extends TurboModule {
    preload: (sources: Source[]) => void
    clearMemoryCache: () => Promise<void>
    clearDiskCache: () => Promise<void>
    getOriginalSize: (source: Source, options: { headers?: { [key: string]: string } }) => Promise<{ width: number; height: number }>
}

export default TurboModuleRegistry.getEnforcing<Spec>('FastImageViewModule')
