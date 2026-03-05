import { StyleSheet, Platform, NativeModules, Image } from 'react-native'
import React from 'react'
import { render } from '@testing-library/react-native'
import FastImage from './index'

const style = StyleSheet.create({ image: { width: 44, height: 44 } })

describe('FastImage (iOS)', () => {
    beforeAll(() => {
        Platform.OS = 'ios'
        NativeModules.FastImageViewModule = {
            preload: Function.prototype,
            clearMemoryCache: Function.prototype,
            clearDiskCache: Function.prototype,
        }
    })

    it('renders', () => {
        const { toJSON } = render(
            <FastImage
                source={{
                    uri: 'https://facebook.github.io/react/img/logo_og.png',
                    headers: {
                        token: 'someToken',
                    },
                    priority: FastImage.priority.high,
                }}
                style={style.image}
            />,
        )
        expect(toJSON()).toMatchSnapshot()
    })

    it('renders a normal Image when not passed a uri', () => {
        const { toJSON } = render(
            <FastImage
                source={require('../ReactNativeFastImageExampleServer/pictures/jellyfish.gif')}
                style={style.image}
            />,
        )
        expect(toJSON()).toMatchSnapshot()
    })

    it('renders Image with fallback prop', () => {
        const { toJSON } = render(
            <FastImage
                source={require('../ReactNativeFastImageExampleServer/pictures/jellyfish.gif')}
                style={style.image}
                fallback
            />,
        )
        expect(toJSON()).toMatchSnapshot()
    })

    it('renders defaultSource', () => {
        const { toJSON } = render(
            <FastImage
                defaultSource={require('../ReactNativeFastImageExampleServer/pictures/jellyfish.gif')}
                style={style.image}
            />,
        )
        expect(toJSON()).toMatchSnapshot()
    })

    it('passes cacheKey to native source', () => {
        const resolveSpy = jest
            .spyOn(Image, 'resolveAssetSource')
            .mockImplementation((src: any) => src)
        const { UNSAFE_getByType } = render(
            <FastImage
                source={{
                    uri: 'https://facebook.github.io/react/img/logo_og.png',
                    cacheKey: 'logo-og',
                }}
                style={style.image}
            />,
        )
        const nativeView = UNSAFE_getByType('FastImageView' as any)
        expect(nativeView.props.source.cacheKey).toBe('logo-og')
        resolveSpy.mockRestore()
    })

    it('ignores null cacheKey in native source', () => {
        const resolveSpy = jest
            .spyOn(Image, 'resolveAssetSource')
            .mockImplementation((src: any) => src)
        const { UNSAFE_getByType } = render(
            <FastImage
                source={{
                    uri: 'https://facebook.github.io/react/img/logo_og.png',
                    cacheKey: null as any,
                }}
                style={style.image}
            />,
        )
        const nativeView = UNSAFE_getByType('FastImageView' as any)
        expect(nativeView.props.source?.cacheKey).toBeUndefined()
        resolveSpy.mockRestore()
    })
})

describe('FastImage (Android)', () => {
    beforeAll(() => {
        Platform.OS = 'android'
    })

    it('renders a normal defaultSource', () => {
        const { toJSON } = render(
            <FastImage
                defaultSource={require('../ReactNativeFastImageExampleServer/pictures/jellyfish.gif')}
                style={style.image}
            />,
        )
        expect(toJSON()).toMatchSnapshot()
    })

    it('renders a normal defaultSource when fails to load source', () => {
        const { toJSON } = render(
            <FastImage
                defaultSource={require('../ReactNativeFastImageExampleServer/pictures/jellyfish.gif')}
                source={{
                    uri: 'https://www.google.com/image_does_not_exist.png',
                }}
                style={style.image}
            />,
        )
        expect(toJSON()).toMatchSnapshot()
    })

    it('renders a non-existing defaultSource', () => {
        const { toJSON } = render(
            <FastImage defaultSource={12345} style={style.image} />,
        )
        expect(toJSON()).toMatchSnapshot()
    })
})
